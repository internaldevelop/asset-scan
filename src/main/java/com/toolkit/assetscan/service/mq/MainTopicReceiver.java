package com.toolkit.assetscan.service.mq;

import com.toolkit.assetscan.global.rabbitmq.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
// 测试联调时，解除 RabbitListener 注释
@RabbitListener(queues = RabbitConfig.DEFAULT_TOPIC )
public class MainTopicReceiver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void process(String message) {
        logger.info("<--- receiver topics: " + RabbitConfig.DEFAULT_TOPIC);
        logger.info("<--- receive message: " + message);
    }
}
