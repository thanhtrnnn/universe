import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  UpdateDateColumn,
  ManyToOne,
  OneToMany,
  JoinColumn,
} from 'typeorm';
import { Class } from '@/modules/courses/entities/class.entity';
import { Attendance } from '@/modules/attendance/entities/attendance.entity';

@Entity('schedules')
export class Schedule {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @ManyToOne(() => Class, (cls) => cls.schedules, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'class_id' })
  classEntity: Class;

  @Column({ name: 'class_id' })
  classId: string;

  /** 0 = Sunday, 1 = Monday, ..., 6 = Saturday */
  @Column({ name: 'day_of_week', type: 'int' })
  dayOfWeek: number;

  @Column({ name: 'start_time', type: 'time' })
  startTime: string;

  @Column({ name: 'end_time', type: 'time' })
  endTime: string;

  @Column()
  room: string;

  @OneToMany(() => Attendance, (attendance) => attendance.schedule)
  attendances: Attendance[];

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date;
}
