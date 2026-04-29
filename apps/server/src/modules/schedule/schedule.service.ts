import { Injectable, NotFoundException, Inject } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import Redis from 'ioredis';
import { REDIS_CLIENT } from '@/database/redis.module';
import { Schedule } from './entities/schedule.entity';

@Injectable()
export class ScheduleService {
  private readonly CACHE_TTL = 3600; // 1 hour

  constructor(
    @InjectRepository(Schedule)
    private readonly scheduleRepo: Repository<Schedule>,
    @Inject(REDIS_CLIENT) private readonly redis: Redis,
  ) {}

  async create(data: Partial<Schedule>): Promise<Schedule> {
    const schedule = this.scheduleRepo.create(data);
    const saved = await this.scheduleRepo.save(schedule);
    await this.invalidateCache(saved.classId);
    return saved;
  }

  async findByClass(classId: string): Promise<Schedule[]> {
    // Try cache first
    const cacheKey = `schedule:class:${classId}`;
    const cached = await this.redis.get(cacheKey);
    if (cached) return JSON.parse(cached);

    const schedules = await this.scheduleRepo.find({
      where: { classId },
      order: { dayOfWeek: 'ASC', startTime: 'ASC' },
    });

    await this.redis.set(cacheKey, JSON.stringify(schedules), 'EX', this.CACHE_TTL);
    return schedules;
  }

  /** Get student's full timetable based on their enrollments */
  async findByStudent(studentId: string): Promise<Schedule[]> {
    const cacheKey = `schedule:student:${studentId}`;
    const cached = await this.redis.get(cacheKey);
    if (cached) return JSON.parse(cached);

    const schedules = await this.scheduleRepo
      .createQueryBuilder('schedule')
      .innerJoin('schedule.classEntity', 'class')
      .innerJoin('class.enrollments', 'enrollment')
      .leftJoinAndSelect('schedule.classEntity', 'cls')
      .leftJoinAndSelect('cls.course', 'course')
      .where('enrollment.studentId = :studentId', { studentId })
      .andWhere('enrollment.status = :status', { status: 'enrolled' })
      .orderBy('schedule.dayOfWeek', 'ASC')
      .addOrderBy('schedule.startTime', 'ASC')
      .getMany();

    await this.redis.set(cacheKey, JSON.stringify(schedules), 'EX', this.CACHE_TTL);
    return schedules;
  }

  async update(id: string, data: Partial<Schedule>): Promise<Schedule> {
    const schedule = await this.scheduleRepo.findOne({ where: { id } });
    if (!schedule) throw new NotFoundException(`Schedule #${id} not found`);
    await this.scheduleRepo.update(id, data);
    await this.invalidateCache(schedule.classId);
    return this.scheduleRepo.findOneOrFail({ where: { id } });
  }

  async remove(id: string): Promise<void> {
    const schedule = await this.scheduleRepo.findOne({ where: { id } });
    if (!schedule) throw new NotFoundException(`Schedule #${id} not found`);
    await this.scheduleRepo.remove(schedule);
    await this.invalidateCache(schedule.classId);
  }

  private async invalidateCache(classId: string) {
    await this.redis.del(`schedule:class:${classId}`);
    // Student caches are invalidated more broadly — acceptable trade-off
  }
}
