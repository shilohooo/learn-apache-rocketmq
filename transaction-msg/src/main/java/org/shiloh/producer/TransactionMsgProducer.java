package org.shiloh.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.apache.rocketmq.client.apis.producer.Transaction;
import org.apache.rocketmq.client.apis.producer.TransactionResolution;
import org.shiloh.constant.MqTransactionMsgConstant;
import org.shiloh.mq.constants.MqCommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * RocketMQ 事务消息生产者
 *
 * @author shiloh
 * @date 2023/5/30 21:17
 */
public class TransactionMsgProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionMsgProducer.class);

    public static void main(String[] args) throws Exception {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        final ClientConfiguration configuration = ClientConfiguration.newBuilder()
                .setEndpoints(MqCommonConstants.ENDPOINT)
                .build();
        // 构建事务生产者：事务消息需要生产者构建一个事务检查器，用于检查确认异常半事务的中间状态
        try (
                final Producer producer = provider.newProducerBuilder()
                        .setClientConfiguration(configuration)
                        .setTopics(MqTransactionMsgConstant.TOPIC)
                        .setTransactionChecker(messageView -> {
                            // 事务检查器一般是根据业务的ID去检查本地事务是否正确提交还是回滚，此处以订单ID属性为例。
                            // 在订单表找到了这个订单，说明本地事务插入订单的操作已经正确提交，如果订单表没有订单，说明本地事务已经回滚。
                            final String orderId = messageView.getProperties().get("orderId");
                            if (StringUtils.isBlank(orderId)) {
                                // 订单 ID 不存在，直接回滚
                                return TransactionResolution.ROLLBACK;
                            }

                            return checkOrderById(orderId)
                                    ? TransactionResolution.COMMIT
                                    : TransactionResolution.ROLLBACK;
                        })
                        .build()
        ) {
            // 开启事务分支
            final Transaction transaction;
            try {
                transaction = producer.beginTransaction();
            } catch (ClientException e) {
                LOGGER.error("开始事务分支失败", e);
                return;
            }
            // 构建消息
            final Message message = provider.newMessageBuilder()
                    .setTopic(MqTransactionMsgConstant.TOPIC)
                    .setTag(MqTransactionMsgConstant.TAG)
                    .setKeys(MqTransactionMsgConstant.MSG_KEY)
                    .setMessageGroup(MqTransactionMsgConstant.CONSUMER_GROUP)
                    // 一般事务消息都会设置一个本地事务关联的唯一ID，用来做本地事务回查的校验
                    .addProperty("orderId", UUID.randomUUID().toString())
                    .setBody("事务消息测试".getBytes(StandardCharsets.UTF_8))
                    .build();
            // 发送半事务消息
            final SendReceipt sendReceipt;
            try {
                sendReceipt = producer.send(message);
                LOGGER.info("事务消息发送结果：{}", sendReceipt);
            } catch (ClientException e) {
                // 半事务消息发送失败，事务可以直接退出并回滚
                LOGGER.error("半事务消息发送失败", e);
                return;
            }
            // 执行本地事务，并确定本地事务结果。
            // 1. 如果本地事务提交成功，则提交消息事务。
            // 2. 如果本地事务提交失败，则回滚消息事务。
            // 3. 如果本地事务未知异常，则不处理，等待事务消息回查。
            final boolean isLocalTransactionOk = doLocalTransaction();
            // 提交事务
            try {
                if (isLocalTransactionOk) {
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } catch (ClientException e) {
                // 业务可以自身对实时性的要求选择是否重试，如果放弃重试，可以依赖事务消息回查机制进行事务状态的提交。
                LOGGER.error("事务消息{}失败", isLocalTransactionOk ? "提交" : "回滚", e);
            }
        }

    }

    /**
     * 模拟订单表查询服务，用于确认订单事务是否提交成功
     *
     * @author shiloh
     * @date 2023/5/30 21:18
     */
    public static boolean checkOrderById(String orderId) {
        LOGGER.info("根据订单ID：{} 查询订单信息...", orderId);
        return true;
    }

    /**
     * 模拟本地事务的执行结果
     *
     * @author shiloh
     * @date 2023/5/30 21:22
     */
    public static boolean doLocalTransaction() {
        return true;
    }
}
