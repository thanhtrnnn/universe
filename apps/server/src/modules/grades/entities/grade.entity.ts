import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  UpdateDateColumn,
  ManyToOne,
  JoinColumn,
} from 'typeorm';
import { Enrollment } from '@/modules/enrollment/entities/enrollment.entity';

export enum GradeType {
  MIDTERM = 'midterm',
  FINAL = 'final',
  ASSIGNMENT = 'assignment',
  QUIZ = 'quiz',
}

@Entity('grades')
export class Grade {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @ManyToOne(() => Enrollment, (enrollment) => enrollment.grades, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'enrollment_id' })
  enrollment: Enrollment;

  @Column({ name: 'enrollment_id' })
  enrollmentId: string;

  @Column({ type: 'enum', enum: GradeType })
  type: GradeType;

  @Column()
  name: string;

  @Column({ type: 'decimal', precision: 5, scale: 2 })
  score: number;

  @Column({ name: 'max_score', type: 'decimal', precision: 5, scale: 2, default: 10 })
  maxScore: number;

  /** Weight as percentage (e.g., 30 = 30%) */
  @Column({ type: 'decimal', precision: 5, scale: 2 })
  weight: number;

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date;
}
