package org.shiloh.constant;

/**
 * RocketMQ顺序消息相关常量
 *
 * @author shiloh
 * @date 2023/5/29 22:25
 */
public final class MqOrderedMsgConstants {
    private MqOrderedMsgConstants() {
    }

    /*
     * 服务端地址
     */
    public static final String ENDPOINT = "172.29.67.83:8081";

    /*
     * 主题
     */
    public static final String TOPIC = "FIFOTopic";

    /*
     * 消费组，同一个消费组内的消息将按照先进先出的方式进行消费
     */
    public static final String CONSUMER_GROUP = "FIFO_ConsumerGroup";

    /*
     * 标签
     */
    public static final String TAG = "FIFOTopicTag";
}
