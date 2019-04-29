package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.dao.helper.TasksManageHelper;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.TaskStatusEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskManageService {
    private ResponseBean responseBean;
    private final TasksMapper tasksMapper;
    private final TasksManageHelper tasksManageHelper;
    private final ResponseHelper responseHelper;

    @Autowired
    public TaskManageService(TasksMapper tasksMapper, TasksManageHelper tasksManageHelper, ResponseHelper responseHelper) {
        this.tasksMapper = tasksMapper;
        this.tasksManageHelper = tasksManageHelper;
        this.responseHelper = responseHelper;
    }

    private boolean iCheckParams(TaskPo taskPo) {
        responseBean = responseHelper.success();
        return true;
    }

    private ResponseBean successReturnTaskInfo(String name, String taskUuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", name);
        jsonData.put("uuid", taskUuid);
        return responseHelper.success(jsonData);
    }


    public ResponseBean getAllTasks() {
        List<TaskPo> taskPoList = tasksMapper.allTasks();
        if ( (taskPoList == null) || (taskPoList.size() == 0))
            responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(taskPoList);
    }

    public ResponseBean getAllTaskDetails() {
        List<TaskInfosDto> taskInfosList = tasksMapper.getAllTaskDto();
        if ( (taskInfosList == null) || (taskInfosList.size() == 0))
            responseHelper.error(ErrorCodeEnum.ERROR_TASK_INFO_NOT_FOUND);

        return responseHelper.success(taskInfosList);
    }

    public ResponseBean addTask(TaskPo taskPo) {
        // 检查参数
        if (!iCheckParams(taskPo))
            return responseBean;

        // TODO: 暂不检查任务重名，以后待定

        // 设置新任务的创建时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        taskPo.setCreate_time(currentTime);

        // 分配任务UUID
        taskPo.setUuid(MyUtils.generateUuid());

        // 新任务状态设置为已激活
        taskPo.setStatus(TaskStatusEnum.TASK_ACTIVE.getStatus());

        // 添加新用户的记录
        if ( !tasksManageHelper.addTask(taskPo) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnTaskInfo(taskPo.getName(), taskPo.getUuid());
    }

    public ResponseBean deleteTask(String taskUuid) {
        // 查找指定的任务是否存在
        TaskPo taskPo = tasksMapper.getTaskByUuid(taskUuid);
        if (taskPo == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        // 删除任务
        if (!tasksManageHelper.deleteTask(taskUuid))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnTaskInfo(taskPo.getName(), taskPo.getUuid());
    }

    public ResponseBean getTaskByUuid(String taskUuid) {
        TaskPo taskPo = tasksMapper.getTaskByUuid(taskUuid);
        if (taskPo == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(taskPo);
    }

    public ResponseBean updateTask(TaskPo taskPo) {
        // 检查参数
        if (!iCheckParams(taskPo))
            return responseBean;

        // 设置任务的更新时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        taskPo.setUpdate_time(currentTime);

        if (!tasksManageHelper.updateTask(taskPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnTaskInfo(taskPo.getName(), taskPo.getUuid());
    }
}
