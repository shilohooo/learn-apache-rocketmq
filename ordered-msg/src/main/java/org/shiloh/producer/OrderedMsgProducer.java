package org.shiloh.producer;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.shiloh.constant.MqOrderedMsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * RocketMQ 顺序消息生产者
 *
 * @author shiloh
 * @date 2023/5/29 22:28
 */
public class OrderedMsgProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderedMsgProducer.class);

    public static void main(String[] args) throws Exception {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(MqOrderedMsgConstants.ENDPOINT)
                .build();
        try (
                final Producer producer = provider.newProducerBuilder()
                        .setClientConfiguration(configuration)
                        .setTopics(MqOrderedMsgConstants.TOPIC)
                        .build()
        ) {
            for (int i = 0; i < 2; i++) {
                // 构建顺序消息
                final String datetime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                final byte[] msgBody = String.format("顺序消息测试 - %s - %d", datetime, (i + 1))
                        .getBytes(StandardCharsets.UTF_8);
                final Message message = provider.newMessageBuilder()
                        .setTopic(MqOrderedMsgConstants.TOPIC)
                        .setTag(MqOrderedMsgConstants.TAG)
                        .setKeys(UUID.randomUUID().toString().replaceAll("-", ""))
                        .setMessageGroup(MqOrderedMsgConstants.CONSUMER_GROUP)
                        .setBody(msgBody)
                        .build();
                // 发现消息，需要关注发送结果，并捕获失败异常
                final SendReceipt sendReceipt = producer.send(message);
                LOGGER.info("【顺序消息】消息发送结果：{}", sendReceipt);
            }
        }
    }
}
