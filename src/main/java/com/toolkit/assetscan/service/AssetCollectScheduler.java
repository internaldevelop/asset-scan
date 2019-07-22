package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.websocket.SockMsgTypeEnum;
import com.toolkit.assetscan.global.websocket.WebSocketServer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
public class AssetCollectScheduler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired
    RestTemplate restTemplate;

    private List<TimerTask> timerTaskList;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler2() {
        return new ThreadPoolTaskScheduler();
    }

    public boolean isSchedulerStartUp() {
        return (timerTaskList != null);
    }

    public boolean initScheduler() {
        if (timerTaskList == null)
            timerTaskList = new ArrayList<>();
        else
            timerTaskList.clear();
        return isSchedulerStartUp();
    }

    public boolean setTask(String assetUuid, String assetIp, String infoTypes) {
        stopTask(assetUuid);
        return startTask(assetUuid, assetIp, infoTypes);
    }

    public boolean startTask(String assetUuid, String assetIp, String infoTypes) {
        // 未指定信息类别时，默认收集CPU使用率和内存使用率
        if (infoTypes == null || infoTypes.isEmpty())
            infoTypes = "Proc CPU Ranking,Proc Memory Ranking,CPU Usage,Mem";

        // 创建一个 Runnable ，设置：任务和项目的 UUID
        MyRunnable runnable = new MyRunnable();
        runnable.setAssetUuid(assetUuid);
        runnable.setAssetIp(assetIp);
        runnable.setInfoTypes(infoTypes);

        // 制定任务计划
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(runnable, new CronTrigger("0/3 * * * * *"));
        if (future == null)
            return false;

        if (timerTaskList == null)
            timerTaskList = new ArrayList<>();

        TimerTask timerTask = new TimerTask();
        timerTask.setAssetUuid(assetUuid);
        timerTask.setFuture(future);
        timerTaskList.add(timerTask);

        return true;
    }

    public boolean stopTask(String assetUuid) {
        if (timerTaskList == null)
            timerTaskList = new ArrayList<>();

        for (TimerTask timerTask : this.timerTaskList) {
            // 在任务计划列表中查找
            if (timerTask.getAssetUuid().equals(assetUuid)) {
                // 如果任务和资产的 UUID 匹配，则取消该任务计划
                ScheduledFuture<?> future = timerTask.getFuture();
                if (future != null)
                    future.cancel(true);
                // 移除任务计划
                timerTaskList.remove(timerTask);
                return true;
            }
        }
        return false;
    }

    private class MyRunnable implements Runnable {
        private String assetUuid;
        private String assetIp;
        private String infoTypes;
        @Override
        public void run() {
            // 构造URL
            String ip = "http://" + this.assetIp + ":8191";
            String url = ip + "/asset-info/acquire?types={types}";

            // 构造参数map
            HashMap<String, String> map = new HashMap<>();
            map.put("types", this.infoTypes);

            // 向节点发送请求，并返回节点的响应结果
            ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
            ResponseBean responseBean = (ResponseBean)responseEntity.getBody();

            // 将节点的资产实时信息通过 websocket 广播到客户端
            if (responseBean.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
                JSONObject jsonMsg = (JSONObject)JSONObject.toJSON(responseBean.getPayload());
                jsonMsg.put("asset_uuid", this.assetUuid);
                WebSocketServer.broadcastAssetInfo(SockMsgTypeEnum.ASSET_REAL_TIME_INFO, jsonMsg);
            }
        }

        public void setAssetUuid(String assetUuid) {
            this.assetUuid = assetUuid;
        }

        public void setAssetIp(String assetIp) {
            this.assetIp = assetIp;
        }

        public void setInfoTypes(String infoTypes) {
            this.infoTypes = infoTypes;
        }
    }

    @Data
    private class TimerTask {
        private ScheduledFuture<?> future;
        private String assetUuid;
    }

}
