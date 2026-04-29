import {
  Controller,
  Get,
  Post,
  Patch,
  Delete,
  Param,
  Body,
  Query,
  UseGuards,
  ParseUUIDPipe,
} from '@nestjs/common';
import { ApiTags, ApiBearerAuth, ApiOperation } from '@nestjs/swagger';
import { CoursesService } from './courses.service';
import { JwtAuthGuard } from '@/common/guards/jwt-auth.guard';
import { RolesGuard } from '@/common/guards/roles.guard';
import { Roles } from '@/common/decorators/roles.decorator';
import { Role } from '@/common/enums/role.enum';

@ApiTags('Courses')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('courses')
export class CoursesController {
  constructor(private readonly coursesService: CoursesService) {}

  // --- Course endpoints ---

  @Post()
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Create a course' })
  createCourse(@Body() body: any) {
    return this.coursesService.createCourse(body);
  }

  @Get()
  @ApiOperation({ summary: 'List all courses' })
  findAllCourses(@Query('page') page?: number, @Query('limit') limit?: number) {
    return this.coursesService.findAllCourses(page, limit);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get course by ID' })
  findCourse(@Param('id', ParseUUIDPipe) id: string) {
    return this.coursesService.findCourseById(id);
  }

  @Patch(':id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Update course' })
  updateCourse(@Param('id', ParseUUIDPipe) id: string, @Body() body: any) {
    return this.coursesService.updateCourse(id, body);
  }

  @Delete(':id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Delete course' })
  removeCourse(@Param('id', ParseUUIDPipe) id: string) {
    return this.coursesService.removeCourse(id);
  }

  // --- Class endpoints ---

  @Post('classes')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Create a class (course offering)' })
  createClass(@Body() body: any) {
    return this.coursesService.createClass(body);
  }

  @Get('classes/list')
  @ApiOperation({ summary: 'List all classes' })
  findAllClasses(
    @Query('courseId') courseId?: string,
    @Query('page') page?: number,
    @Query('limit') limit?: number,
  ) {
    return this.coursesService.findAllClasses(courseId, page, limit);
  }

  @Get('classes/:id')
  @ApiOperation({ summary: 'Get class by ID' })
  findClass(@Param('id', ParseUUIDPipe) id: string) {
    return this.coursesService.findClassById(id);
  }

  @Patch('classes/:id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Update class' })
  updateClass(@Param('id', ParseUUIDPipe) id: string, @Body() body: any) {
    return this.coursesService.updateClass(id, body);
  }

  @Delete('classes/:id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Delete class' })
  removeClass(@Param('id', ParseUUIDPipe) id: string) {
    return this.coursesService.removeClass(id);
  }
}
