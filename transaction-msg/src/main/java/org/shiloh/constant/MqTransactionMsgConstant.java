package org.shiloh.constant;

/**
 * RocketMQ 事务消息相关常量
 *
 * @author shiloh
 * @date 2023/5/30 21:15
 */
public final class MqTransactionMsgConstant {
    private MqTransactionMsgConstant() {}

    /*
     * 主题
     */
    public static final String TOPIC = "TestTransactionTopic";

    /*
     * 消费组
     */
    public static final String CONSUMER_GROUP = "TransactionConsumerGroup";

    /*
     * 消息标签
     */
    public static final String TAG = "TransactionMsgTag";

    /*
     * 消息 Key
     */
    public static final String MSG_KEY = "TransactionMsgKey";
}
