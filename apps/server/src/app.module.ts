import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';

// Infrastructure
import { DatabaseModule } from './database/database.module';
import { MongoModule } from './database/mongo.module';
import { RedisModule } from './database/redis.module';
import { KafkaModule } from './kafka/kafka.module';

// Feature modules
import { AuthModule } from './modules/auth/auth.module';
import { UsersModule } from './modules/users/users.module';
import { CoursesModule } from './modules/courses/courses.module';
import { EnrollmentModule } from './modules/enrollment/enrollment.module';
import { ScheduleModule } from './modules/schedule/schedule.module';
import { GradesModule } from './modules/grades/grades.module';
import { AttendanceModule } from './modules/attendance/attendance.module';
import { ChatModule } from './modules/chat/chat.module';
import { NotificationsModule } from './modules/notifications/notifications.module';
import { ActivityLogsModule } from './modules/activity-logs/activity-logs.module';

@Module({
  imports: [
    // Global config — .env loaded once, available everywhere via ConfigService
    ConfigModule.forRoot({ isGlobal: true }),

    // Database connections
    DatabaseModule,
    MongoModule,
    RedisModule,

    // Event streaming
    KafkaModule,

    // Auth & Users
    AuthModule,
    UsersModule,

    // Academic
    CoursesModule,
    EnrollmentModule,
    ScheduleModule,
    GradesModule,

    // Smart Attendance
    AttendanceModule,

    // Realtime & Communication
    ChatModule,
    NotificationsModule,

    // Audit
    ActivityLogsModule,
  ],
})
export class AppModule {}
