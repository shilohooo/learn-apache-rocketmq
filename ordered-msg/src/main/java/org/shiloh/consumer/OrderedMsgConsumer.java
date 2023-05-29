package org.shiloh.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.shiloh.constant.MqOrderedMsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * RocketMQ 顺序消息消费者
 *
 * @author shiloh
 * @date 2023/5/29 22:37
 */
public class OrderedMsgConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderedMsgConsumer.class);

    public static void main(String[] args) throws Exception {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(MqOrderedMsgConstants.ENDPOINT)
                .build();
        final FilterExpression expression = new FilterExpression(MqOrderedMsgConstants.TAG, FilterExpressionType.TAG);
        final PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup(MqOrderedMsgConstants.CONSUMER_GROUP)
                .setSubscriptionExpressions(Collections.singletonMap(MqOrderedMsgConstants.TOPIC, expression))
                .setMessageListener(messageView -> {
                    LOGGER.info("【顺序消息】收到顺序消息：{}", messageView);
                    final ByteBuffer byteBuffer = messageView.getBody();
                    final byte[] data = new byte[byteBuffer.limit()];
                    int i = 0;
                    while (byteBuffer.hasRemaining()) {
                        data[i] = byteBuffer.get();
                        i++;
                    }
                    LOGGER.info("【顺序消息】消费者接收到的数据为：{}", new String(data, StandardCharsets.UTF_8));
                    return ConsumeResult.SUCCESS;
                })
                .build();
        Thread.sleep(Long.MAX_VALUE);
        pushConsumer.close();
    }
}
