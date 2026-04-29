import {
  Controller,
  Post,
  Get,
  Patch,
  Param,
  Body,
  UseGuards,
  ParseUUIDPipe,
} from '@nestjs/common';
import { ApiTags, ApiBearerAuth, ApiOperation } from '@nestjs/swagger';
import { EnrollmentService } from './enrollment.service';
import { JwtAuthGuard } from '@/common/guards/jwt-auth.guard';
import { RolesGuard } from '@/common/guards/roles.guard';
import { Roles } from '@/common/decorators/roles.decorator';
import { CurrentUser } from '@/common/decorators/current-user.decorator';
import { Role } from '@/common/enums/role.enum';

@ApiTags('Enrollment')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('enrollments')
export class EnrollmentController {
  constructor(private readonly enrollmentService: EnrollmentService) {}

  @Post()
  @Roles(Role.STUDENT)
  @ApiOperation({ summary: 'Enroll in a class' })
  enroll(
    @CurrentUser('id') studentId: string,
    @Body('classId') classId: string,
  ) {
    return this.enrollmentService.enroll(studentId, classId);
  }

  @Get('my')
  @Roles(Role.STUDENT)
  @ApiOperation({ summary: 'Get my enrollments' })
  findMine(@CurrentUser('id') studentId: string) {
    return this.enrollmentService.findByStudent(studentId);
  }

  @Get('class/:classId')
  @Roles(Role.LECTURER, Role.ADMIN)
  @ApiOperation({ summary: 'Get students enrolled in a class' })
  findByClass(@Param('classId', ParseUUIDPipe) classId: string) {
    return this.enrollmentService.findByClass(classId);
  }

  @Patch(':id/drop')
  @Roles(Role.STUDENT, Role.ADMIN)
  @ApiOperation({ summary: 'Drop a class' })
  drop(@Param('id', ParseUUIDPipe) id: string) {
    return this.enrollmentService.drop(id);
  }
}
