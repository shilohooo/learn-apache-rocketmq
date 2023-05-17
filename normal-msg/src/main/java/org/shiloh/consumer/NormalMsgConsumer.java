package org.shiloh.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.shiloh.producer.NormalMsgProducer;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * 普通消息消费者
 *
 * @author shiloh
 * @date 2023/5/13 16:55
 */
public class NormalMsgConsumer {
    public static void main(String[] args) throws Exception {
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints("172.23.192.104:8081")
                .build();
        final FilterExpression expression = new FilterExpression(NormalMsgProducer.MSG_TAG, FilterExpressionType.TAG);
        final PushConsumer pushConsumer = ClientServiceProvider.loadService()
                .newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup("NormalMsgConsumerGroup")
                .setSubscriptionExpressions(Collections.singletonMap(NormalMsgProducer.TOPIC, expression))
                .setMessageListener(messageView -> {
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

                    return ConsumeResult.SUCCESS;
                })
                .build();
        Thread.sleep(Long.MAX_VALUE);
        pushConsumer.close();
    }
}
