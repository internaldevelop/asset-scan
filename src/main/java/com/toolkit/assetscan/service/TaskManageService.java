package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.dao.helper.TasksManageHelper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
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
    private final AssetsMapper assetsMapper;

    @Autowired
    public TaskManageService(TasksMapper tasksMapper, TasksManageHelper tasksManageHelper, ResponseHelper responseHelper, AssetsMapper assetsMapper) {
        this.tasksMapper = tasksMapper;
        this.tasksManageHelper = tasksManageHelper;
        this.responseHelper = responseHelper;
        this.assetsMapper = assetsMapper;
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

    private boolean iCheckTaskDetailsParam(TaskInfosDto taskInfosDto) {
        // TODO:
        // 检查创建用户是否有效（create_user_uuid）

        // policy不能为空，检查是否有效（policy UUID）

        // 检查资产信息是否完备（以下二选一）
        // (1) asset_uuid 为有效的资产
        // (2) 资产名称、IP、端口、OS种类、版本、系统登录用户和密码）

        // 检查任务自身信息是否完备（任务名称）

        return true;
    }

    /**
     * 增加一条新资产，并返回该资产的UUID
     * @param taskInfosDto 任务 dto 对象，从中提取资产信息
     * @return boolean true: 成功，并设置 taskInfosDto.asset_uuid
     *                  false: 失败
     */
    private boolean iAddAsset(TaskInfosDto taskInfosDto) {
        // 创建资产PO对象，并设置其属性
        AssetPo assetPo = new AssetPo();
        assetPo.setName(taskInfosDto.getAsset_name());
        assetPo.setIp(taskInfosDto.getAsset_ip());
        assetPo.setPort(taskInfosDto.getAsset_port());
        assetPo.setOs_type(taskInfosDto.getAsset_os_type());
        assetPo.setOs_ver(taskInfosDto.getAsset_os_ver());
        assetPo.setUser(taskInfosDto.getAsset_login_user());
        assetPo.setPassword(taskInfosDto.getAsset_login_pwd());
        // 分配资产UUID
        String asset_uuid = MyUtils.generateUuid();
        assetPo.setUuid(asset_uuid);

        // 添加新资产的记录
        if (assetsMapper.addAsset(assetPo) <= 0) {
            return false;
        } else {
            taskInfosDto.setAsset_uuid(asset_uuid);
            return true;
        }
    }

    public ResponseBean addTaskDetails(TaskInfosDto taskInfosDto) {
        // 检查DTO信息是否完备
        if (!iCheckTaskDetailsParam(taskInfosDto)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);
        }

        // asset_uuid 为空时，需要新建资产记录
        String assetUuid = taskInfosDto.getAsset_uuid();
        if (assetUuid == null || assetUuid.length() == 0) {
            if (!iAddAsset(taskInfosDto))
                return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 新建任务的记录
        return addTask((TaskPo)taskInfosDto);
    }

    public ResponseBean updateTaskDetails(TaskInfosDto taskInfosDto) {
        // 检查DTO信息是否完备
        if (!iCheckTaskDetailsParam(taskInfosDto)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);
        }

        // asset_uuid 为空时，需要新建资产记录
        String assetUuid = taskInfosDto.getAsset_uuid();
        if (assetUuid == null || assetUuid.length() == 0) {
            if (!iAddAsset(taskInfosDto))
                return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 更新任务记录
        return updateTask((TaskPo) taskInfosDto);
    }
}
