import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Course } from './entities/course.entity';
import { Class } from './entities/class.entity';

@Injectable()
export class CoursesService {
  constructor(
    @InjectRepository(Course)
    private readonly courseRepo: Repository<Course>,
    @InjectRepository(Class)
    private readonly classRepo: Repository<Class>,
  ) {}

  // --- Course CRUD ---

  async createCourse(data: Partial<Course>): Promise<Course> {
    const course = this.courseRepo.create(data);
    return this.courseRepo.save(course);
  }

  async findAllCourses(page = 1, limit = 20) {
    const [data, total] = await this.courseRepo.findAndCount({
      skip: (page - 1) * limit,
      take: limit,
      order: { code: 'ASC' },
    });
    return { data, total };
  }

  async findCourseById(id: string): Promise<Course> {
    const course = await this.courseRepo.findOne({
      where: { id },
      relations: ['classes'],
    });
    if (!course) throw new NotFoundException(`Course #${id} not found`);
    return course;
  }

  async updateCourse(id: string, data: Partial<Course>): Promise<Course> {
    await this.findCourseById(id);
    await this.courseRepo.update(id, data);
    return this.findCourseById(id);
  }

  async removeCourse(id: string): Promise<void> {
    const course = await this.findCourseById(id);
    await this.courseRepo.remove(course);
  }

  // --- Class CRUD ---

  async createClass(data: Partial<Class>): Promise<Class> {
    const cls = this.classRepo.create(data);
    return this.classRepo.save(cls);
  }

  async findAllClasses(courseId?: string, page = 1, limit = 20) {
    const qb = this.classRepo
      .createQueryBuilder('class')
      .leftJoinAndSelect('class.course', 'course')
      .leftJoinAndSelect('class.lecturer', 'lecturer')
      .orderBy('class.createdAt', 'DESC')
      .skip((page - 1) * limit)
      .take(limit);

    if (courseId) {
      qb.where('class.courseId = :courseId', { courseId });
    }

    const [data, total] = await qb.getManyAndCount();
    return { data, total };
  }

  async findClassById(id: string): Promise<Class> {
    const cls = await this.classRepo.findOne({
      where: { id },
      relations: ['course', 'lecturer', 'schedules'],
    });
    if (!cls) throw new NotFoundException(`Class #${id} not found`);
    return cls;
  }

  async updateClass(id: string, data: Partial<Class>): Promise<Class> {
    await this.findClassById(id);
    await this.classRepo.update(id, data);
    return this.findClassById(id);
  }

  async removeClass(id: string): Promise<void> {
    const cls = await this.findClassById(id);
    await this.classRepo.remove(cls);
  }
}
