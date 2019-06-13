package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.global.redis.IRedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class TaskRunStatusService {
    @Autowired
    private IRedisClient redisClient;

    private String _getTaskRedisKey(String taskUuid) {
        return "task_run_" + taskUuid;
    }

    private String _getTaskIndexRedisKey() {
        return "task_run_index";
    }

    public TaskRunStatusDto getTaskRunStatus(String taskUuid, String projectUuid) {
        String key = _getTaskRedisKey(taskUuid);
        String value = (String)redisClient.get(key);
        JSONObject jsonObject = JSONObject.parseObject(value);
        TaskRunStatusDto taskRunStatusDto = null;
        if (jsonObject != null ) {
            taskRunStatusDto = jsonObject.getObject(projectUuid, TaskRunStatusDto.class);
        }
        return taskRunStatusDto;
    }

    public List<TaskRunStatusDto> getTasksListRunStatus(String tasksUuidList, String projectUuid) {
        String[] uuidArray = tasksUuidList.split(",");
//        List<String> uuidLists = JSONObject.parseArray(tasksUuidList, String.class);
        List<TaskRunStatusDto> runStatusDtoList = new ArrayList<>();
        for(String taskUuid: uuidArray) {
            // 跳过空串
            if (taskUuid.isEmpty())
                continue;

            // 调用单个任务的状态
            TaskRunStatusDto runStatusDto = getTaskRunStatus(taskUuid, projectUuid);
            if (runStatusDto != null) {
                runStatusDtoList.add(runStatusDto);
            }
        }
        return runStatusDtoList;
    }

    public List<TaskRunStatusDto> getAllTasksRunStatus(String projectUuid) {
        List<TaskRunStatusDto> runStatusDtoList = new ArrayList<>();
        // 获取运行状态信息的任务UUID索引
        Map<String, Serializable> map = redisClient.getMap(_getTaskIndexRedisKey());
        // 没有索引，返回运行信息的空列表
        if (map == null)
            return runStatusDtoList;

        //
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            // 枚举 map 中的 key
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if ( (key == null) || (key.isEmpty()) || (value == null) || !(value.equals("1")))
                continue;

            // 获取索引中有效UUID的任务执行信息
            TaskRunStatusDto runStatusDto = getTaskRunStatus(key, projectUuid);
            if (runStatusDto != null) {
                runStatusDtoList.add(runStatusDto);
            }
        }
        return runStatusDtoList;
    }

    public boolean setTaskRunStatus(TaskRunStatusDto taskRunStatusDto) {
        // 记录有运行状态的任务索引（task_uuid）
        Map<String, Serializable> map = new HashMap<>();
        map.put(taskRunStatusDto.getTask_uuid(), "1");
        if (!redisClient.addMap(_getTaskIndexRedisKey(), map))
            return false;

        // 记录该任务的执行状态（新建对象或追加）
        JSONObject jsonObject;
        String key = _getTaskRedisKey(taskRunStatusDto.getTask_uuid());
        String value = (String)redisClient.get(key);
        if (value != null && !value.isEmpty())
            jsonObject = JSONObject.parseObject(value);
        else
            jsonObject = new JSONObject();
        jsonObject.put(taskRunStatusDto.getProject_uuid(), taskRunStatusDto);
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
