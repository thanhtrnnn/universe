import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  CreateDateColumn,
  UpdateDateColumn,
  OneToMany,
} from 'typeorm';
import { Class } from './class.entity';

@Entity('courses')
export class Course {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column({ unique: true })
  code: string;

  @Column()
  name: string;

  @Column({ type: 'int' })
  credits: number;

  @Column({ type: 'text', nullable: true })
  description: string;

  @Column()
  department: string;

  @Column({ name: 'is_active', default: true })
  isActive: boolean;

  @OneToMany(() => Class, (cls) => cls.course)
  classes: Class[];

  @CreateDateColumn({ name: 'created_at' })
  createdAt: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt: Date;
}
