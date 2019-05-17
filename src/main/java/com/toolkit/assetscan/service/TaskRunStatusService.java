package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.global.redis.IRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskRunStatusService {
    @Autowired
    private IRedisClient redisClient;

    private String _getTaskRedisKey(String taskUuid) {
        return "task_run_" + taskUuid;
    }

    public TaskRunStatusDto getTaskRunStatus(String taskUuid) {
        String key = _getTaskRedisKey(taskUuid);
        String value = (String)redisClient.get(key);
        JSONObject jsonObject = JSONObject.parseObject(value);
        TaskRunStatusDto taskRunStatusDto = jsonObject.getObject("status", TaskRunStatusDto.class);
        return taskRunStatusDto;
    }

    public List<TaskRunStatusDto> getTasksListRunStatus(String tasksUuidList) {
        String[] uuidArray = tasksUuidList.split(",");
//        List<String> uuidLists = JSONObject.parseArray(tasksUuidList, String.class);
        List<TaskRunStatusDto> runStatusDtoList = new ArrayList<>();
        for(String taskUuid: uuidArray) {
            // 跳过空串
            if (taskUuid.isEmpty())
                continue;

            // 调用单个任务的状态
            TaskRunStatusDto runStatusDto = getTaskRunStatus(taskUuid);
            if (runStatusDto != null) {
                runStatusDtoList.add(runStatusDto);
            }
        }
        return runStatusDtoList;
    }

    public boolean setTaskRunStatus(String taskUuid, TaskRunStatusDto taskRunStatusDto) {
        String key = _getTaskRedisKey(taskUuid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", taskRunStatusDto);
        return redisClient.set(key, jsonObject.toJSONString());
    }

    public String getString(String taskUuid) {
        String key = _getTaskRedisKey(taskUuid);
        return (String) redisClient.get(key, 0);
    }

    public boolean setString(String taskUuid, String value) {
        String key = _getTaskRedisKey(taskUuid);
        return redisClient.set(key, value);
    }
}
