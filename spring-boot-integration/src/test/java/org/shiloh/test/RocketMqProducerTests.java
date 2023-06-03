package org.shiloh.test;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.shiloh.constant.MqNormalMsgConstants;
import org.shiloh.mq.constants.SymbolConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * MQ 消息生产测试
 *
 * @author shiloh
 * @date 2023/6/3 17:35
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RocketMqProducerTests {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqProducerTests.class);

    /**
     * 测试发送消息到 MQ
     *
     * @author shiloh
     * @date 2023/6/3 17:36
     */
    @Test
    public void test() throws Exception {
        final String dest = MqNormalMsgConstants.TOPIC + SymbolConstants.COLON + MqNormalMsgConstants.Tag.NORMAL_MSG_TEST_TAG;
        final String msg = String.format("普通消息测试 - %s", new Date());
        this.rocketMQTemplate.send(dest, new GenericMessage<>(msg));
        LOGGER.info("普通消息发送成功");
        TimeUnit.SECONDS.sleep(5L);
    }
}
