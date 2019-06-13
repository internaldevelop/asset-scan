package com.toolkit.assetscan.service;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Component
public class TaskExecuteScheduler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Autowired private TaskManageService taskManageService;
//    private ScheduledFuture<?> future;
    private List<FutureTask> futureTaskList;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    /**
     * @param runTime 格式强制为 HH:mm:ss
     * @return
     */
    private String convertTimeToCronExpr(String runTime) {
        return runTime.substring(6, 8) + " " +
                runTime.substring(3, 5) + " " +
                runTime.substring(0, 2) + " " +
                "* * *";
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        Date date = null;
//        try {
//            date = new Date(dateFormat.parse(runTime).getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "";
//        }
//        return date.getSeconds()
    }

    public String startTask(String taskUuid, String projectUuid, String userUuid, String runTime) {
        // 创建一个 Runnable ，设置：任务和项目的 UUID
        MyRunnable runnable = new MyRunnable();
        runnable.setTaskUuid(taskUuid);
        runnable.setProjectUuid(projectUuid);
        runnable.setUserUuid(userUuid);

        // 从时间转换成 Cron 的 expression
        String cronExpr = convertTimeToCronExpr(runTime);

        // 制定任务计划
        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(runnable, new CronTrigger(cronExpr));
//        ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(runnable, new CronTrigger("0/5 * * * * *"));
        if (future == null)
            return "Scheduled task start failed";

        if (futureTaskList == null)
            futureTaskList = new ArrayList<>();

        // 保存任务计划及相关参数
        FutureTask futureTask = new FutureTask();
        futureTask.setTaskUuid(taskUuid);
        futureTask.setProjectUuid(projectUuid);
        futureTask.setRunTime(runTime);
        futureTask.setFuture(future);
        futureTaskList.add(futureTask);

        return "start: Task: " + taskUuid + "\tProject: " + projectUuid;
    }

    public String setTask(String taskUuid, String projectUuid, String userUuid, String runTime) {
        stopTask(taskUuid, projectUuid);

        return startTask(taskUuid, projectUuid, userUuid, runTime);
    }

    public String stopTask(String taskUuid, String projectUuid) {
        if (this.futureTaskList == null)
            futureTaskList = new ArrayList<>();

        for (FutureTask futureTask : this.futureTaskList) {
            // 在任务计划列表中查找
            if ( (futureTask.getTaskUuid().equals(taskUuid)) && (futureTask.getProjectUuid().equals(projectUuid)) )  {
                // 如果任务和项目的 UUID匹配，则取消该任务计划
                ScheduledFuture<?> future = futureTask.getFuture();
                if (future != null)
                    future.cancel(true);
                // 移除任务计划
                futureTaskList.remove(futureTask);
                return "stop: Task: " + taskUuid + "\tProject: " + projectUuid;
            }
        }

        return "Scheduled task not found";
    }

    private class MyRunnable implements Runnable {
        private String taskUuid;
        private String projectUuid;
        private String userUuid;
        @Override
        public void run() {
            logger.info("Running: Task: " + this.taskUuid + "\tProject: " + this.projectUuid);
            taskManageService.executeSingleTask(this.projectUuid, this.taskUuid, this.userUuid);
        }

        public void setTaskUuid(String taskUuid) {
            this.taskUuid = taskUuid;
        }

        public void setProjectUuid(String projectUuid) {
            this.projectUuid = projectUuid;
        }

        public void setUserUuid(String userUuid) {
            this.userUuid = userUuid;
        }
    }

    @Data
    private class FutureTask {
        private ScheduledFuture<?> future;
        private String taskUuid;
        private String projectUuid;
        private String runTime;
    }

}
