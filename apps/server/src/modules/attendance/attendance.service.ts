import {
  Injectable,
  BadRequestException,
  NotFoundException,
  Inject,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { ConfigService } from '@nestjs/config';
import * as crypto from 'crypto';
import Redis from 'ioredis';
import { REDIS_CLIENT } from '@/database/redis.module';
import { Attendance, AttendanceStatus } from './entities/attendance.entity';
import { Schedule } from '@/modules/schedule/entities/schedule.entity';

export interface QrPayload {
  sessionId: string;
  timestamp: number;
  signature: string;
}

export interface CheckInDto {
  scheduleId: string;
  qrToken: string;
  latitude: number;
  longitude: number;
}

@Injectable()
export class AttendanceService {
  private readonly QR_TTL = 5; // seconds
  private readonly GEO_THRESHOLD = 50; // meters
  private readonly hmacSecret: string;

  constructor(
    @InjectRepository(Attendance)
    private readonly attendanceRepo: Repository<Attendance>,
    @InjectRepository(Schedule)
    private readonly scheduleRepo: Repository<Schedule>,
    @Inject(REDIS_CLIENT) private readonly redis: Redis,
    private readonly config: ConfigService,
  ) {
    this.hmacSecret = this.config.get('JWT_SECRET', 'default-secret');
  }

  // ─── QR Code Generation (Lecturer calls this) ───

  /**
   * Generate a dynamic QR token for a schedule session.
   * Token rotates every 5 seconds — signed with HMAC-SHA256.
   */
  async generateQrToken(scheduleId: string): Promise<QrPayload> {
    const schedule = await this.scheduleRepo.findOne({
      where: { id: scheduleId },
      relations: ['classEntity'],
    });
    if (!schedule) throw new NotFoundException(`Schedule #${scheduleId} not found`);

    const timestamp = Date.now();
    const sessionId = `${scheduleId}:${new Date().toISOString().split('T')[0]}`;
    const dataToSign = `${sessionId}:${timestamp}`;
    const signature = crypto
      .createHmac('sha256', this.hmacSecret)
      .update(dataToSign)
      .digest('hex');

    const payload: QrPayload = { sessionId, timestamp, signature };

    // Store in Redis with 5s TTL so it auto-expires
    await this.redis.set(
      `qr:${signature}`,
      JSON.stringify(payload),
      'EX',
      this.QR_TTL,
    );

    return payload;
  }

  // ─── Student Check-in ───

  async checkIn(studentId: string, dto: CheckInDto): Promise<Attendance> {
    // 1. Validate QR token (Layer 1: Dynamic QR)
    const storedRaw = await this.redis.get(`qr:${dto.qrToken}`);
    if (!storedRaw) {
      throw new BadRequestException('QR code expired or invalid');
    }

    const stored: QrPayload = JSON.parse(storedRaw);

    // Verify HMAC signature
    const expectedData = `${stored.sessionId}:${stored.timestamp}`;
    const expectedSig = crypto
      .createHmac('sha256', this.hmacSecret)
      .update(expectedData)
      .digest('hex');

    if (expectedSig !== dto.qrToken) {
      throw new BadRequestException('QR code signature mismatch');
    }

    // 2. Validate GPS location (Layer 2: Geo-fencing with Haversine)
    const schedule = await this.scheduleRepo.findOne({
      where: { id: dto.scheduleId },
      relations: ['classEntity'],
    });
    if (!schedule) throw new NotFoundException('Schedule not found');

    const classroomLat = Number(schedule.classEntity.latitude);
    const classroomLng = Number(schedule.classEntity.longitude);
    const distance = this.haversineDistance(
      dto.latitude,
      dto.longitude,
      classroomLat,
      classroomLng,
    );

    if (distance > this.GEO_THRESHOLD) {
      throw new BadRequestException(
        `Too far from classroom: ${distance.toFixed(1)}m (max ${this.GEO_THRESHOLD}m)`,
      );
    }

    // 3. Record attendance
    const sessionDate = new Date().toISOString().split('T')[0];
    const existing = await this.attendanceRepo.findOne({
      where: { scheduleId: dto.scheduleId, studentId, sessionDate },
    });
    if (existing) {
      throw new BadRequestException('Already checked in for this session');
    }

    const attendance = this.attendanceRepo.create({
      scheduleId: dto.scheduleId,
      studentId,
      sessionDate,
      status: AttendanceStatus.PRESENT,
      qrToken: dto.qrToken,
      gpsLatitude: dto.latitude,
      gpsLongitude: dto.longitude,
      distance,
      scannedAt: new Date(),
    });

    return this.attendanceRepo.save(attendance);
  }

  // ─── Queries ───

  async findBySchedule(scheduleId: string, sessionDate?: string) {
    const where: any = { scheduleId };
    if (sessionDate) where.sessionDate = sessionDate;
    return this.attendanceRepo.find({
      where,
      relations: ['student'],
      order: { scannedAt: 'ASC' },
    });
  }

  async findByStudent(studentId: string) {
    return this.attendanceRepo.find({
      where: { studentId },
      relations: ['schedule', 'schedule.classEntity', 'schedule.classEntity.course'],
      order: { sessionDate: 'DESC' },
    });
  }

  // ─── Haversine Formula (real GPS distance calculation) ───

  /**
   * Calculate the great-circle distance between two GPS coordinates
   * using the Haversine formula. Returns distance in meters.
   *
   * Reference: AGENT.md mandates real Haversine — no mocks.
   */
  private haversineDistance(
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number,
  ): number {
    const R = 6371000; // Earth's radius in meters
    const toRad = (deg: number) => (deg * Math.PI) / 180;

    const dLat = toRad(lat2 - lat1);
    const dLon = toRad(lon2 - lon1);
    const a =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2;
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}
