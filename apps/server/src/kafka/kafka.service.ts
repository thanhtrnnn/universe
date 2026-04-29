import { Injectable, OnModuleInit, OnModuleDestroy, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Kafka, Producer, Consumer, EachMessagePayload } from 'kafkajs';

@Injectable()
export class KafkaService implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(KafkaService.name);
  private kafka: Kafka;
  private producer: Producer;
  private consumer: Consumer;
  private handlers = new Map<string, (payload: any) => Promise<void>>();

  constructor(private readonly config: ConfigService) {
    this.kafka = new Kafka({
      clientId: 'universe-server',
      brokers: [this.config.get('KAFKA_BROKER', 'localhost:9092')],
    });
    this.producer = this.kafka.producer();
    this.consumer = this.kafka.consumer({ groupId: 'universe-group' });
  }

  async onModuleInit() {
    try {
      await this.producer.connect();
      this.logger.log('Kafka producer connected');

      await this.consumer.connect();
      this.logger.log('Kafka consumer connected');

      // Subscribe to registered topics
      for (const topic of this.handlers.keys()) {
        await this.consumer.subscribe({ topic, fromBeginning: false });
      }

      await this.consumer.run({
        eachMessage: async (payload: EachMessagePayload) => {
          const handler = this.handlers.get(payload.topic);
          if (handler && payload.message.value) {
            const data = JSON.parse(payload.message.value.toString());
            await handler(data);
          }
        },
      });
    } catch (error) {
      this.logger.warn('Kafka not available — running without event streaming');
    }
  }

  async onModuleDestroy() {
    await this.producer.disconnect().catch(() => {});
    await this.consumer.disconnect().catch(() => {});
  }

  async publish(topic: string, message: any): Promise<void> {
    await this.producer.send({
      topic,
      messages: [{ value: JSON.stringify(message) }],
    });
  }

  registerHandler(topic: string, handler: (payload: any) => Promise<void>) {
    this.handlers.set(topic, handler);
  }
}
