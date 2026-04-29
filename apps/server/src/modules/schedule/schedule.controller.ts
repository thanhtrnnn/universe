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
import { ScheduleService } from './schedule.service';
import { JwtAuthGuard } from '@/common/guards/jwt-auth.guard';
import { RolesGuard } from '@/common/guards/roles.guard';
import { Roles } from '@/common/decorators/roles.decorator';
import { CurrentUser } from '@/common/decorators/current-user.decorator';
import { Role } from '@/common/enums/role.enum';

@ApiTags('Schedule')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('schedules')
export class ScheduleController {
  constructor(private readonly scheduleService: ScheduleService) {}

  @Post()
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Create a schedule entry' })
  create(@Body() body: any) {
    return this.scheduleService.create(body);
  }

  @Get('class/:classId')
  @ApiOperation({ summary: 'Get schedule for a class' })
  findByClass(@Param('classId', ParseUUIDPipe) classId: string) {
    return this.scheduleService.findByClass(classId);
  }

  @Get('my')
  @Roles(Role.STUDENT)
  @ApiOperation({ summary: 'Get my timetable' })
  findMine(@CurrentUser('id') studentId: string) {
    return this.scheduleService.findByStudent(studentId);
  }

  @Patch(':id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Update schedule entry' })
  update(@Param('id', ParseUUIDPipe) id: string, @Body() body: any) {
    return this.scheduleService.update(id, body);
  }

  @Delete(':id')
  @Roles(Role.ADMIN)
  @ApiOperation({ summary: 'Delete schedule entry' })
  remove(@Param('id', ParseUUIDPipe) id: string) {
    return this.scheduleService.remove(id);
  }
}
