import {
  WebSocketGateway,
  WebSocketServer,
  OnGatewayConnection,
  OnGatewayDisconnect,
} from '@nestjs/websockets';
import { Logger, OnModuleInit } from '@nestjs/common';
import { Server, Socket } from 'socket.io';
import { KafkaService } from '@/kafka/kafka.service';

/**
 * Socket.IO gateway that receives Kafka events and pushes
 * notifications to connected clients in realtime.
 */
@WebSocketGateway({
  namespace: '/notifications',
  cors: { origin: '*' },
})
export class NotificationsGateway
  implements OnGatewayConnection, OnGatewayDisconnect, OnModuleInit
{
  @WebSocketServer()
  server: Server;

  private readonly logger = new Logger(NotificationsGateway.name);
  private onlineUsers = new Map<string, string>();

  constructor(private readonly kafka: KafkaService) {}

  onModuleInit() {
    // Register Kafka consumer handler for class-notifications topic
    this.kafka.registerHandler('class-notifications', async (payload) => {
      const socketId = this.onlineUsers.get(payload.userId);
      if (socketId) {
        // User is online — push via Socket.IO
        this.server.to(socketId).emit('notification', payload);
      } else {
        // User is offline — FCM push notification would go here
        this.logger.debug(
          `User ${payload.userId} offline, queued for FCM push`,
        );
      }
    });
  }

  handleConnection(client: Socket) {
    const userId = client.handshake.query.userId as string;
    if (userId) {
      this.onlineUsers.set(userId, client.id);
    }
  }

  handleDisconnect(client: Socket) {
    const userId = client.handshake.query.userId as string;
    if (userId) {
      this.onlineUsers.delete(userId);
    }
  }
}
