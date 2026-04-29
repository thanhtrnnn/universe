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
import { Course } from './course.entity';
import { User } from '@/modules/users/entities/user.entity';
import { Enrollment } from '@/modules/enrollment/entities/enrollment.entity';
import { Schedule } from '@/modules/schedule/entities/schedule.entity';

@Entity('classes')
export class Class {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  code: string;

  @ManyToOne(() => Course, (course) => course.classes, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'course_id' })
  course: Course;

  @Column({ name: 'course_id' })
  courseId: string;

  @ManyToOne(() => User)
  @JoinColumn({ name: 'lecturer_id' })
  lecturer: User;

  @Column({ name: 'lecturer_id' })
  lecturerId: string;

  @Column()
  semester: string;

  @Column()
  room: string;

  @Column({ name: 'max_students', type: 'int', default: 40 })
  maxStudents: number;

  /** Geo-fencing: classroom coordinates for attendance verification */
  @Column({ type: 'decimal', precision: 10, scale: 7 })
  latitude: number;

  @Column({ type: 'decimal', precision: 10, scale: 7 })
  longitude: number;

  @Column({ name: 'is_active', default: true })
  isActive: boolean;

  @OneToMany(() => Enrollment, (enrollment) => enrollment.classEntity)
  enrollments: Enrollment[];

  @OneToMany(() => Schedule, (schedule) => schedule.classEntity)
  schedules: Schedule[];

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date;
}
