//package com.toolkit.assetscan.global.rabbitmq;
//
//import com.toolkit.assetscan.global.rabbitmq.config.RabbitConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitHandler;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//@RabbitListener(queues = RabbitConfig.MAIN_QUEUE)
//public class MsgConsumer {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private IMsgConsumerProcess iMsgConsumerProcess;
//
//    @RabbitHandler
//    public void process(String content) {
//        iMsgConsumerProcess.process(content);
////        logger.info("接收处理队列A当中的消息： " + content);
//    }
//}
