package org.shiloh.constant;

/**
 * MQ 普通消息相关常量
 *
 * @author shiloh
 * @date 2023/6/3 17:23
 */
public final class MqNormalMsgConstants {
    private MqNormalMsgConstants() {
    }

    /**
     * 主题
     */
    public static final String TOPIC = "ShilohNormalMsg";

    /**
     * 消费者分组
     *
     * @author shiloh
     * @date 2023/6/3 17:26
     */
    public static class ConsumerGroup {
        /**
         * 普通消息测试分组
         */
        public static final String NORMAL_MSG_TEST_GROUP = "NormalMsgTestGroup";
    }

    /**
     * 标签
     *
     * @author shiloh
     * @date 2023/6/3 17:26
     */
    public static class Tag {
        /**
         * 普通消息测试标签
         */
        public static final String NORMAL_MSG_TEST_TAG = "NormalMsgTestTag";
    }
}
