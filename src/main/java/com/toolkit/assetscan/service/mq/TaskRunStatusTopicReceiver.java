package com.toolkit.assetscan.service.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.global.rabbitmq.config.RabbitConfig;
import com.toolkit.assetscan.global.websocket.SockMsgTypeEnum;
import com.toolkit.assetscan.global.websocket.WebSocketServer;
import com.toolkit.assetscan.service.TaskRunStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RabbitListener(queues = RabbitConfig.TASK_RUN_STATUS_TOPIC)
public class TaskRunStatusTopicReceiver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TaskRunStatusService taskRunStatusService;

    @RabbitHandler
    public void process(String message) {
        logger.info("<--- receiver topics: " + RabbitConfig.TASK_RUN_STATUS_TOPIC);
        logger.info("<--- receive message: " + message);

        try {
            // 把收到的数据解析成JSON对象
//            JSONObject jsonMessage = JSONObject.parseObject(message);
//            if (jsonMessage == null)
//                return;

            // 获取任务运行状态对象
//            TaskRunStatusDto taskRunStatusDto = jsonMessage.getObject("status", TaskRunStatusDto.class);
            TaskRunStatusDto taskRunStatusDto = JSON.parseObject(message, new TypeReference<TaskRunStatusDto>() {});
            if (taskRunStatusDto != null) {
                // 获取消息通知的任务的运行状态，发送给所有客户端
//                TaskRunStatusDto runStatus = taskRunStatusService.getTaskRunStatus(taskRunStatusDto.getTask_uuid());
                WebSocketServer.sendInfo(SockMsgTypeEnum.SINGLE_TASK_RUN_INFO, taskRunStatusDto, null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
    }
}
