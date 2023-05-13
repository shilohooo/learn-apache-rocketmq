package org.shiloh.consumer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collections;

/**
 * RocketMQ消费者代码示例
 * <p>
 * 创建订阅普通消息的消费者
 *
 * @author shiloh
 * @date 2023/5/2 23:11
 */
public class PushConsumerExample01 {
    private static final Logger LOGGER = LoggerFactory.getLogger(PushConsumerExample01.class);

    public static void main(String[] args) throws Exception {
        // 接入点地址：需要设置成Proxy的地址和端口列表，一般是：xxx:8081;xxx:8081
        final String endpoint = "172.29.67.83:8081";
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoint)
                .build();
        // 订阅消息的过滤规则，“*”表示订阅所有Tag的消息
        final String tag = "*";
        final FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
        // 为消费者指定所属的消费者分组，Group 需要提前创建
        final String consumerGroup = "TestGroup";
        // 指定需要订阅哪个目标Topic，Topic需要提前创建
        final String topic = "TestTopic";
        // 初始化PushConsumer，需要绑定消费者分组、通信参数以及订阅关系
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                .setClientConfiguration(configuration)
                // 设置消费者分组
                .setConsumerGroup(consumerGroup)
                // 设置预绑定的订阅关系
                .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                // 设置消费监听器
                .setMessageListener(messageView -> {
                    try {
                        // 处理消息并返回消费结果
                        LOGGER.info("Consume message successfully, messageId = {}", messageView.getMessageId());
                        final ByteBuffer byteBuffer = messageView.getBody();
                        System.out.println(byteBuffer.isReadOnly());
                        final byte[] data = new byte[byteBuffer.limit()];
                        int i = 0;
                        while (byteBuffer.hasRemaining()) {
                            data[i] = byteBuffer.get();
                            i++;
                        }
                        System.out.println(new String(data));
                        return ConsumeResult.SUCCESS;
                    } catch (Exception e) {
                        LOGGER.error("消费失败：", e);
                        return ConsumeResult.FAILURE;
                    }
                })
                .build();
        Thread.sleep(Long.MAX_VALUE);
        // 如果不需要再使用 PushConsumer，可以关闭该实例
        pushConsumer.close();
    }
}
