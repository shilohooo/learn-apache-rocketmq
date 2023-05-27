package org.shiloh.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.*;
import org.shiloh.constant.MqDelayMsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;

/**
 * 延迟消息消费者
 *
 * @author shiloh
 * @date 2023/5/27 14:34
 */
public class DelayMsgPushConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayMsgPushConsumer.class);

    public static void main(String[] args) throws Exception {
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints("172.29.67.83:8081")
                .build();
        final FilterExpression expression = new FilterExpression(MqDelayMsgConstants.TAG, FilterExpressionType.TAG);
        final PushConsumer pushConsumer = ClientServiceProvider.loadService()
                .newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                .setConsumerGroup(MqDelayMsgConstants.CONSUMER_GROUP)
                .setSubscriptionExpressions(Collections.singletonMap(MqDelayMsgConstants.TOPIC, expression))
                .setMessageListener(getMessageListener())
                .build();
        System.out.println(pushConsumer.getSubscriptionExpressions());
        Thread.sleep(Long.MAX_VALUE);
        pushConsumer.close();
    }

    /**
     * 构建消息监听器
     *
     * @return {@link MessageListener}
     * @author shiloh
     * @date 2023/5/27 14:45
     */
    private static MessageListener getMessageListener() {
        return messageView -> {
            messageView.getDeliveryTimestamp()
                    .ifPresent(timestamp -> LOGGER.info(
                            "【延迟消息】deliveryTimestamp = {}", new Date(timestamp)
                    ));
            // 消息消费
            final ByteBuffer byteBuffer = messageView.getBody();
            final byte[] data = new byte[byteBuffer.limit()];
            int i = 0;
            while (byteBuffer.hasRemaining()) {
                data[i] = byteBuffer.get();
                i++;
            }
            LOGGER.info("【延迟消息】消费者接收到的数据为：{}", new String(data));

            return ConsumeResult.SUCCESS;
        };
    }
}
