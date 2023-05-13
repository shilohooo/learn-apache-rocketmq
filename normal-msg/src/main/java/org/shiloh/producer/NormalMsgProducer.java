package org.shiloh.producer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 普通消息示例 - 生产者
 *
 * @author shiloh
 * @date 2023/5/8 18:14
 */
public class NormalMsgProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NormalMsgProducer.class);

    public static final String TOPIC = "NormalMsgTopic";
    public static final String MSG_KEY = "NormalMsgKey";
    public static final String MSG_TAG = "NormalMsgTag";

    public static void main(String[] args) {
        final ClientServiceProvider clientServiceProvider = ClientServiceProvider.loadService();
        final ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints("172.23.192.104:8081")
                .build();
        try (
                final Producer producer = clientServiceProvider
                        .newProducerBuilder()
                        .setClientConfiguration(clientConfiguration)
                        .build()
        ) {
            // 普通消息发送。
            final MessageBuilder messageBuilder = clientServiceProvider.newMessageBuilder();
            final Message message = messageBuilder.setTopic(TOPIC)
                    // 设置消息索引键，可根据关键字精确查找某条消息。
                    .setKeys(MSG_KEY)
                    // 设置消息Tag，用于消费端根据指定Tag过滤消息。
                    .setTag(MSG_TAG)
                    // 消息体。
                    .setBody("NormalMsg".getBytes())
                    .build();
            // 发送消息，需要关注发送结果，并捕获失败等异常。
            final SendReceipt sendReceipt = producer.send(message);
            LOGGER.info("发生普通消息，发送结果：{}", sendReceipt.getMessageId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
