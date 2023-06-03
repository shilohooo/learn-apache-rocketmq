package org.shiloh.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.shiloh.constant.MqNormalMsgConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * MQ 普通消息测试消费者
 *
 * @author shiloh
 * @date 2023/6/3 17:28
 */
@Component
@RocketMQMessageListener(
        consumerGroup = MqNormalMsgConstants.ConsumerGroup.NORMAL_MSG_TEST_GROUP,
        topic = MqNormalMsgConstants.TOPIC,
        selectorExpression = MqNormalMsgConstants.Tag.NORMAL_MSG_TEST_TAG
)
public class MqNormalMsgTestConsumer implements RocketMQListener<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqNormalMsgTestConsumer.class);

    /**
     * 消息处理
     *
     * @param msg 消息内容
     * @author shiloh
     * @date 2023/6/3 17:29
     */
    @Override
    public void onMessage(String msg) {
        LOGGER.info("【MQ普通消息消费】消费者接收到的消息数据为：{}", msg);
    }
}
