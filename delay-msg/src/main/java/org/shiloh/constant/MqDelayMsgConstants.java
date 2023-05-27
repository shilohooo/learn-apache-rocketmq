package org.shiloh.constant;

/**
 * RocketMQ延迟消息相关常量
 *
 * @author shiloh
 * @date 2023/5/27 12:05
 */
public final class MqDelayMsgConstants {
    private MqDelayMsgConstants() {
    }

    /*
     * 主题
     */
    public static final String TOPIC = "DelayTopic";

    /*
     * 标签
     */
    public static final String TAG = "DelayMsgTag";

    /*
     * 消息索引
     */
    public static final String MSG_KEY = "DelayMsgKey";

    /*
     * 消费者分组
     */
    public static final String CONSUMER_GROUP = "DelayMsgConsumerGroup";
}
