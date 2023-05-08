package org.shiloh.producer;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * RocketMQ生产者示例代码
 * <p>
 * 发送普通消息
 *
 * @author shiloh
 * @date 2023/5/2 22:17
 */
public class ProducerExample01 {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerExample01.class);

    public static void main(String[] args) throws ClientException {
        // 接入点地址：需要设置成Proxy的地址和端口列表，一般是：xxx:8081;xxx:8081
        final String endpoint = "172.23.192.104:8081";
        // 消息发送的目标Topic名称，需要提前创建
        final String topic = "TestTopic";

        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(endpoint)
                .build();
        // 初始化生产者时需要设置通信配置以及预绑定的Topic
        try (final Producer producer = provider.newProducerBuilder()
                .setTopics(topic)
                .setClientConfiguration(configuration)
                .build()) {
            // 普通消息发送
            final Message message = provider.newMessageBuilder()
                    .setTopic(topic)
                    // 设置消息索引键，可根据关键字精确查找某条消息
                    .setKeys("MessageKey")
                    // 设置消息 Tag，用于消费端根据指定 Tag 过滤消息
                    .setTag("MessageTag")
                    // 消息体
                    .setBody("MessageBody".getBytes(StandardCharsets.UTF_8))
                    .build();
            final SendReceipt sendReceipt = producer.send(message);
            LOGGER.info("Send message successfully, messageId = {}", sendReceipt.getMessageId());
        } catch (IOException e) {
            LOGGER.error("Failed to send message", e);
        }
    }
}
