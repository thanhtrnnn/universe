import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Message, MessageDocument } from './schemas/message.schema';

@Injectable()
export class ChatService {
  constructor(
    @InjectModel(Message.name)
    private readonly messageModel: Model<MessageDocument>,
  ) {}

  async saveMessage(data: {
    senderId: string;
    receiverId: string;
    content: string;
    type?: string;
  }): Promise<Message> {
    return this.messageModel.create(data);
  }

  async getConversation(
    userId1: string,
    userId2: string,
    page = 1,
    limit = 50,
  ) {
    return this.messageModel
      .find({
        $or: [
          { senderId: userId1, receiverId: userId2 },
          { senderId: userId2, receiverId: userId1 },
        ],
      })
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit)
      .exec();
  }

  async markAsRead(messageId: string, userId: string): Promise<void> {
    await this.messageModel.updateOne(
      { _id: messageId, receiverId: userId },
      { readAt: new Date() },
    );
  }

  async getUnreadCount(userId: string): Promise<number> {
    return this.messageModel.countDocuments({
      receiverId: userId,
      readAt: null,
    });
  }
}
