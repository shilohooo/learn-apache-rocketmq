package org.shiloh.producer;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.shiloh.constant.MqDelayMsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 延迟消息生产者
 *
 * @author shiloh
 * @date 2023/5/27 12:07
 */
public class DelayMsgProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelayMsgProducer.class);

    public static void main(String[] args) {
        final ClientServiceProvider clientServiceProvider = ClientServiceProvider.loadService();
        final ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints("172.29.67.83:8081")
                .build();
        try (
                final Producer producer = clientServiceProvider
                        .newProducerBuilder()
                        .setTopics(MqDelayMsgConstants.TOPIC)
                        .setClientConfiguration(clientConfiguration)
                        .build()
        ) {
            // 发送定时 / 延时消息
            final Date deliveryTime = DateUtils.addMinutes(new Date(), 1);
            LOGGER.info("deliveryTime = {}", deliveryTime);
            final Message delayMsg = clientServiceProvider.newMessageBuilder()
                    .setTopic(MqDelayMsgConstants.TOPIC)
                    .setTag(MqDelayMsgConstants.TAG)
                    .setKeys(MqDelayMsgConstants.MSG_KEY)
                    // 设置定时 / 延迟发送时间为1分钟后
                    .setDeliveryTimestamp(deliveryTime.getTime())
                    .setBody("延迟消息测试".getBytes(StandardCharsets.UTF_8))
                    .build();
            final SendReceipt sendReceipt = producer.send(delayMsg);
            LOGGER.info("延迟消息发送结果：msgId: = {}", sendReceipt.getMessageId());
        } catch (IOException | ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
