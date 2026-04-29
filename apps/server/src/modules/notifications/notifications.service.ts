import { Injectable, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import {
  Notification,
  NotificationDocument,
  NotificationType,
} from './schemas/notification.schema';
import { KafkaService } from '@/kafka/kafka.service';

const KAFKA_TOPIC = 'class-notifications';

@Injectable()
export class NotificationsService {
  private readonly logger = new Logger(NotificationsService.name);

  constructor(
    @InjectModel(Notification.name)
    private readonly notificationModel: Model<NotificationDocument>,
    private readonly kafka: KafkaService,
  ) {}

  /**
   * Create notification and publish to Kafka for fan-out.
   * Flow: Save to MongoDB → Publish Kafka → Consumer pushes via Socket.IO / FCM
   */
  async create(data: {
    userId: string;
    title: string;
    body: string;
    type?: NotificationType;
    data?: Record<string, unknown>;
  }) {
    // Step 1: Persist in MongoDB
    const notification = await this.notificationModel.create(data);

    // Step 2: Publish to Kafka topic for realtime delivery
    try {
      await this.kafka.publish(KAFKA_TOPIC, {
        notificationId: notification._id.toString(),
        userId: data.userId,
        title: data.title,
        body: data.body,
        type: data.type,
      });
    } catch (error) {
      this.logger.warn('Kafka publish failed, notification saved to DB only');
    }

    return notification;
  }

  /** Broadcast to multiple users (e.g., all students in a class) */
  async broadcast(
    userIds: string[],
    title: string,
    body: string,
    type: NotificationType = NotificationType.ANNOUNCEMENT,
    data?: Record<string, unknown>,
  ) {
    const notifications = userIds.map((userId) => ({
      userId,
      title,
      body,
      type,
      data: data || {},
      isRead: false,
    }));

    const saved = await this.notificationModel.insertMany(notifications);

    // Publish all to Kafka
    for (const notif of saved) {
      try {
        await this.kafka.publish(KAFKA_TOPIC, {
          notificationId: notif._id.toString(),
          userId: notif.userId,
          title,
          body,
          type,
        });
      } catch {
        // Kafka failures don't block — notifications are persisted
      }
    }

    return { sent: saved.length };
  }

  async findByUser(userId: string, page = 1, limit = 20) {
    return this.notificationModel
      .find({ userId })
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit)
      .exec();
  }

  async markAsRead(notificationId: string, userId: string) {
    return this.notificationModel.updateOne(
      { _id: notificationId, userId },
      { isRead: true },
    );
  }

  async markAllAsRead(userId: string) {
    return this.notificationModel.updateMany(
      { userId, isRead: false },
      { isRead: true },
    );
  }

  async getUnreadCount(userId: string): Promise<number> {
    return this.notificationModel.countDocuments({ userId, isRead: false });
  }
}
