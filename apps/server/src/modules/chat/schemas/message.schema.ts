import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type MessageDocument = HydratedDocument<Message>;

@Schema({ collection: 'messages', timestamps: true })
export class Message {
  @Prop({ required: true, index: true })
  senderId: string;

  @Prop({ required: true, index: true })
  receiverId: string;

  @Prop({ required: true })
  content: string;

  @Prop({ enum: ['text', 'image', 'file'], default: 'text' })
  type: string;

  @Prop({ default: null })
  readAt: Date;
}

export const MessageSchema = SchemaFactory.createForClass(Message);

// Compound index for fetching conversation between two users
MessageSchema.index({ senderId: 1, receiverId: 1, createdAt: -1 });
