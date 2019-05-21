//package com.toolkit.assetscan.service;
//
//import com.toolkit.assetscan.global.rabbitmq.IMsgConsumerProcess;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class TaskRunMQService implements IMsgConsumerProcess {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public void process(String content) {
//        logger.info("接收处理队列当中的消息： " + content);
//    }
//}
