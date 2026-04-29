import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  ManyToOne,
  JoinColumn,
} from 'typeorm';
import { Schedule } from '@/modules/schedule/entities/schedule.entity';
import { User } from '@/modules/users/entities/user.entity';

export enum AttendanceStatus {
  PRESENT = 'present',
  ABSENT = 'absent',
  LATE = 'late',
}

@Entity('attendances')
export class Attendance {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @ManyToOne(() => Schedule, (schedule) => schedule.attendances, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'schedule_id' })
  schedule: Schedule;

  @Column({ name: 'schedule_id' })
  scheduleId: string;

  @ManyToOne(() => User, (user) => user.attendances, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'student_id' })
  student: User;

  @Column({ name: 'student_id' })
  studentId: string;

  @Column({ name: 'session_date', type: 'date' })
  sessionDate: string;

  @Column({
    type: 'enum',
    enum: AttendanceStatus,
    default: AttendanceStatus.ABSENT,
  })
  status: AttendanceStatus;

  /** The QR token that was scanned */
  @Column({ name: 'qr_token', nullable: true })
  qrToken: string;

  /** GPS coordinates submitted by the student's device */
  @Column({ name: 'gps_latitude', type: 'decimal', precision: 10, scale: 7, nullable: true })
  gpsLatitude: number;

  @Column({ name: 'gps_longitude', type: 'decimal', precision: 10, scale: 7, nullable: true })
  gpsLongitude: number;

  /** Haversine distance from classroom in meters */
  @Column({ type: 'decimal', precision: 8, scale: 2, nullable: true })
  distance: number;

  @Column({ name: 'scanned_at', type: 'timestamp', nullable: true })
  scannedAt: Date;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;
}
