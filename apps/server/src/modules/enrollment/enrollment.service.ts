import {
  Injectable,
  NotFoundException,
  ConflictException,
  BadRequestException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Enrollment, EnrollmentStatus } from './entities/enrollment.entity';
import { Class } from '@/modules/courses/entities/class.entity';

@Injectable()
export class EnrollmentService {
  constructor(
    @InjectRepository(Enrollment)
    private readonly enrollmentRepo: Repository<Enrollment>,
    @InjectRepository(Class)
    private readonly classRepo: Repository<Class>,
  ) {}

  async enroll(studentId: string, classId: string): Promise<Enrollment> {
    // Check class exists and has capacity
    const cls = await this.classRepo.findOne({ where: { id: classId } });
    if (!cls) throw new NotFoundException(`Class #${classId} not found`);

    const currentCount = await this.enrollmentRepo.count({
      where: { classId, status: EnrollmentStatus.ENROLLED },
    });
    if (currentCount >= cls.maxStudents) {
      throw new BadRequestException('Class is full');
    }

    // Check duplicate enrollment
    const existing = await this.enrollmentRepo.findOne({
      where: { studentId, classId },
    });
    if (existing) {
      throw new ConflictException('Already enrolled in this class');
    }

    const enrollment = this.enrollmentRepo.create({ studentId, classId });
    return this.enrollmentRepo.save(enrollment);
  }

  async findByStudent(studentId: string) {
    return this.enrollmentRepo.find({
      where: { studentId },
      relations: ['classEntity', 'classEntity.course', 'classEntity.lecturer'],
      order: { enrolledAt: 'DESC' },
    });
  }

  async findByClass(classId: string) {
    return this.enrollmentRepo.find({
      where: { classId },
      relations: ['student'],
      order: { enrolledAt: 'ASC' },
    });
  }

  async drop(id: string): Promise<Enrollment> {
    const enrollment = await this.enrollmentRepo.findOne({ where: { id } });
    if (!enrollment) throw new NotFoundException(`Enrollment #${id} not found`);
    enrollment.status = EnrollmentStatus.DROPPED;
    return this.enrollmentRepo.save(enrollment);
  }
}
