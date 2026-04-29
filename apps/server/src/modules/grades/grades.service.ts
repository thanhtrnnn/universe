import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Grade } from './entities/grade.entity';

@Injectable()
export class GradesService {
  constructor(
    @InjectRepository(Grade)
    private readonly gradeRepo: Repository<Grade>,
  ) {}

  async create(data: Partial<Grade>): Promise<Grade> {
    const grade = this.gradeRepo.create(data);
    return this.gradeRepo.save(grade);
  }

  async findByEnrollment(enrollmentId: string): Promise<Grade[]> {
    return this.gradeRepo.find({
      where: { enrollmentId },
      order: { createdAt: 'ASC' },
    });
  }

  async update(id: string, data: Partial<Grade>): Promise<Grade> {
    const grade = await this.gradeRepo.findOne({ where: { id } });
    if (!grade) throw new NotFoundException(`Grade #${id} not found`);
    await this.gradeRepo.update(id, data);
    return this.gradeRepo.findOneOrFail({ where: { id } });
  }

  async remove(id: string): Promise<void> {
    const grade = await this.gradeRepo.findOne({ where: { id } });
    if (!grade) throw new NotFoundException(`Grade #${id} not found`);
    await this.gradeRepo.remove(grade);
  }

  /** Calculate weighted average for an enrollment */
  async calculateAverage(enrollmentId: string): Promise<number> {
    const grades = await this.findByEnrollment(enrollmentId);
    if (grades.length === 0) return 0;

    let totalWeight = 0;
    let weightedSum = 0;

    for (const grade of grades) {
      const normalized = (Number(grade.score) / Number(grade.maxScore)) * 10;
      weightedSum += normalized * Number(grade.weight);
      totalWeight += Number(grade.weight);
    }

    return totalWeight > 0 ? weightedSum / totalWeight : 0;
  }
}
