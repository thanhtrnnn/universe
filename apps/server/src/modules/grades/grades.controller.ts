import {
  Controller,
  Get,
  Post,
  Patch,
  Delete,
  Param,
  Body,
  UseGuards,
  ParseUUIDPipe,
} from '@nestjs/common';
import { ApiTags, ApiBearerAuth, ApiOperation } from '@nestjs/swagger';
import { GradesService } from './grades.service';
import { JwtAuthGuard } from '@/common/guards/jwt-auth.guard';
import { RolesGuard } from '@/common/guards/roles.guard';
import { Roles } from '@/common/decorators/roles.decorator';
import { Role } from '@/common/enums/role.enum';

@ApiTags('Grades')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('grades')
export class GradesController {
  constructor(private readonly gradesService: GradesService) {}

  @Post()
  @Roles(Role.LECTURER, Role.ADMIN)
  @ApiOperation({ summary: 'Create a grade entry' })
  create(@Body() body: any) {
    return this.gradesService.create(body);
  }

  @Get('enrollment/:enrollmentId')
  @ApiOperation({ summary: 'Get grades for an enrollment' })
  findByEnrollment(@Param('enrollmentId', ParseUUIDPipe) enrollmentId: string) {
    return this.gradesService.findByEnrollment(enrollmentId);
  }

  @Get('enrollment/:enrollmentId/average')
  @ApiOperation({ summary: 'Calculate weighted average for enrollment' })
  getAverage(@Param('enrollmentId', ParseUUIDPipe) enrollmentId: string) {
    return this.gradesService.calculateAverage(enrollmentId);
  }

  @Patch(':id')
  @Roles(Role.LECTURER, Role.ADMIN)
  @ApiOperation({ summary: 'Update a grade' })
  update(@Param('id', ParseUUIDPipe) id: string, @Body() body: any) {
    return this.gradesService.update(id, body);
  }

  @Delete(':id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Delete a grade' })
  remove(@Param('id', ParseUUIDPipe) id: string) {
    return this.gradesService.remove(id);
  }
}
