import {
  Controller,
  Get,
  Post,
  Param,
  Body,
  Query,
  UseGuards,
  ParseUUIDPipe,
} from '@nestjs/common';
import { ApiTags, ApiBearerAuth, ApiOperation } from '@nestjs/swagger';
import { AttendanceService } from './attendance.service';
import { JwtAuthGuard } from '@/common/guards/jwt-auth.guard';
import { RolesGuard } from '@/common/guards/roles.guard';
import { Roles } from '@/common/decorators/roles.decorator';
import { CurrentUser } from '@/common/decorators/current-user.decorator';
import { Role } from '@/common/enums/role.enum';

@ApiTags('Attendance')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard, RolesGuard)
@Controller('attendance')
export class AttendanceController {
  constructor(private readonly attendanceService: AttendanceService) {}

  @Post('qr/generate/:scheduleId')
  @Roles(Role.LECTURER)
  @ApiOperation({ summary: 'Generate dynamic QR token for a session (5s TTL)' })
  generateQr(@Param('scheduleId', ParseUUIDPipe) scheduleId: string) {
    return this.attendanceService.generateQrToken(scheduleId);
  }

  @Post('check-in')
  @Roles(Role.STUDENT)
  @ApiOperation({ summary: 'Student check-in: scan QR + GPS verification' })
  checkIn(
    @CurrentUser('id') studentId: string,
    @Body() body: { scheduleId: string; qrToken: string; latitude: number; longitude: number },
  ) {
    return this.attendanceService.checkIn(studentId, body);
  }

  @Get('schedule/:scheduleId')
  @Roles(Role.LECTURER, Role.ADMIN)
  @ApiOperation({ summary: 'Get attendance records for a schedule session' })
  findBySchedule(
    @Param('scheduleId', ParseUUIDPipe) scheduleId: string,
    @Query('date') date?: string,
  ) {
    return this.attendanceService.findBySchedule(scheduleId, date);
  }

  @Get('my')
  @Roles(Role.STUDENT)
  @ApiOperation({ summary: 'Get my attendance history' })
  findMine(@CurrentUser('id') studentId: string) {
    return this.attendanceService.findByStudent(studentId);
  }
}
