package org.shiloh.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.*;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.shiloh.producer.NormalMsgProducer;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * 普通消息 SimpleConsumer - 主动获取消息消费
 *
 * @author shiloh
 * @date 2023/5/16 18:05
 */
public class NormalMsgSimpleConsumer {
    public static void main(String[] args) throws Exception {
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints("172.23.192.104:8081")
                .build();
        final FilterExpression expression = new FilterExpression(NormalMsgProducer.MSG_TAG, FilterExpressionType.TAG);
        final SimpleConsumer simpleConsumer = ClientServiceProvider.loadService()
                .newSimpleConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup("NormalMsgConsumerGroup")
                .setSubscriptionExpressions(Collections.singletonMap(NormalMsgProducer.TOPIC, expression))
                .setAwaitDuration(Duration.ofSeconds(30L))
                .build();
        final List<MessageView> messageViews = simpleConsumer.receive(10, Duration.ofSeconds(30L));
        messageViews.forEach(messageView -> {
            // 消息消费
            System.out.println("messageView.getMessageId() = " + messageView.getMessageId());
            final ByteBuffer byteBuffer = messageView.getBody();
            final byte[] data = new byte[byteBuffer.limit()];
            int i = 0;
            while (byteBuffer.hasRemaining()) {
                data[i] = byteBuffer.get();
                i++;
            }

            System.out.println("消费者接收到的数据为：" + new String(data));
            // 消息处理完成后，需要主动调用 ACK 提交消费结构
            try {
                simpleConsumer.ack(messageView);
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        });
        simpleConsumer.close();
    }
}
