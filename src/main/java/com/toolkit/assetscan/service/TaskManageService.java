package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.bean.po.ProjectPo;
import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.dao.helper.TasksManageHelper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.ProjectRunTimeModeEnum;
import com.toolkit.assetscan.global.enumeration.TaskRunStatusEnum;
import com.toolkit.assetscan.global.enumeration.TaskStatusEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

@Component
public class TaskManageService {

    @Autowired
    private HttpServletRequest httpServletRequest;
    private ResponseBean responseBean;
    private final TasksMapper tasksMapper;
    private final TasksManageHelper tasksManageHelper;
    private final ResponseHelper responseHelper;
    private final AssetsMapper assetsMapper;
    private final RestTemplate restTemplate;
    @Autowired
    private TaskRunStatusService taskRunStatusService;

    @Autowired
    public TaskManageService(TasksMapper tasksMapper, TasksManageHelper tasksManageHelper, ResponseHelper responseHelper, AssetsMapper assetsMapper, RestTemplate restTemplate) {
        this.tasksMapper = tasksMapper;
        this.tasksManageHelper = tasksManageHelper;
        this.responseHelper = responseHelper;
        this.assetsMapper = assetsMapper;
        this.restTemplate = restTemplate;
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
        if ((taskPoList == null) || (taskPoList.size() == 0))
            responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(taskPoList);
    }

    public ResponseBean getAllTaskDetails() {
        List<TaskInfosDto> taskInfosList = tasksMapper.getAllTaskDto();
        if ((taskInfosList == null) || (taskInfosList.size() == 0))
            responseHelper.error(ErrorCodeEnum.ERROR_TASK_INFO_NOT_FOUND);

        return responseHelper.success(taskInfosList);
    }

    public ResponseBean addTask(TaskPo taskPo) {
        // 检查参数
        if (!iCheckParams(taskPo))
            return responseBean;

        // 检查任务重名
        if (tasksMapper.getTaskNameCount(taskPo.getName()) > 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NAME_EXIST);

        // 设置新任务的创建时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        taskPo.setCreate_time(currentTime);

        // 分配任务UUID
        taskPo.setUuid(MyUtils.generateUuid());

        // 新任务状态设置为已激活
        taskPo.setStatus(TaskStatusEnum.TASK_ACTIVE.getStatus());

        // 添加新用户的记录
        if (!tasksManageHelper.addTask(taskPo))
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

    public ResponseBean getTaskInfoByUuid(String taskUuid) {
        TaskInfosDto taskInfosDto = tasksMapper.getTaskDtoByUuid(taskUuid);
        if (taskInfosDto == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);
        return responseHelper.success(taskInfosDto);
    }

    public ResponseBean updateTask(TaskPo taskPo) {
        // 检查参数
        if (!iCheckParams(taskPo))
            return responseBean;

        // 检查任务重名
        if (tasksMapper.checkNameInOtherTasks(taskPo.getName(), taskPo.getUuid()) > 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NAME_EXIST);

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
     *
     * @param taskInfosDto 任务 dto 对象，从中提取资产信息
     * @return boolean true: 成功，并设置 taskInfosDto.asset_uuid
     * false: 失败
     */
    private ErrorCodeEnum iAddOrUpdateAsset(TaskInfosDto taskInfosDto) {
        String assetUuid = taskInfosDto.getAsset_uuid();
        // 检查资产名称是否可用
        if (assetUuid == null || assetUuid.length() == 0) {
            if (assetsMapper.getAssetNameCount(taskInfosDto.getAsset_name()) > 0)
                return ErrorCodeEnum.ERROR_ASSET_NAME_EXIST;
        } else {
            if (assetsMapper.checkNameInOtherAssets(taskInfosDto.getAsset_name(), assetUuid) > 0)
                return ErrorCodeEnum.ERROR_ASSET_NAME_EXIST;
        }

        // 创建资产PO对象，并设置其属性
        AssetPo assetPo = new AssetPo();
        assetPo.setName(taskInfosDto.getAsset_name());
        assetPo.setIp(taskInfosDto.getAsset_ip());
        assetPo.setPort(taskInfosDto.getAsset_port());
        assetPo.setOs_type(taskInfosDto.getAsset_os_type());
        assetPo.setOs_ver(taskInfosDto.getAsset_os_ver());
        assetPo.setUser(taskInfosDto.getAsset_login_user());
        assetPo.setPassword(taskInfosDto.getAsset_login_pwd());

        if (assetUuid == null || assetUuid.length() == 0) {
            // 新资产，分配资产UUID
            assetUuid = MyUtils.generateUuid();
            assetPo.setUuid(assetUuid);
            // 添加新资产的记录
            if (assetsMapper.addAsset(assetPo) <= 0) {
                return ErrorCodeEnum.ERROR_FAILED_ADD_ASSET;
            } else {
                taskInfosDto.setAsset_uuid(assetUuid);
            }
        } else {
            // 已有资产，更新资产信息
            assetPo.setUuid(assetUuid);
            if (assetsMapper.updateAsset(assetPo) <= 0) {
                return ErrorCodeEnum.ERROR_FAILED_UPDATE_ASSET;
            }
        }

        return ErrorCodeEnum.ERROR_OK;
    }


    public ResponseBean addTaskDetails(TaskInfosDto taskInfosDto) {
        // 检查DTO信息是否完备
        if (!iCheckTaskDetailsParam(taskInfosDto)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);
        }

        // 新建或更新资产记录
        ErrorCodeEnum errorCode = iAddOrUpdateAsset(taskInfosDto);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return responseHelper.error(errorCode);

        // 新建任务的记录
        return addTask((TaskPo) taskInfosDto);
    }

    public ResponseBean updateTaskDetails(TaskInfosDto taskInfosDto) {
        // 检查DTO信息是否完备
        if (!iCheckTaskDetailsParam(taskInfosDto)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);
        }

        // 新建或更新资产记录
        ErrorCodeEnum errorCode = iAddOrUpdateAsset(taskInfosDto);
        if (errorCode != ErrorCodeEnum.ERROR_OK)
            return responseHelper.error(errorCode);

        // 更新任务记录
        return updateTask((TaskPo) taskInfosDto);
    }

//    public ResponseBean runTask(String taskUuid) {
//        // 根据任务UUID，获取任务DTO
//        TaskInfosDto taskInfosDto = tasksMapper.getTaskDtoByUuid(taskUuid);
//        if (taskInfosDto == null)
//            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);
//
//        // 设置任务为空闲状态
//        TaskRunStatusDto taskRunStatusDto = new TaskRunStatusDto();
//        taskRunStatusDto.setTask_uuid(taskUuid);
//        taskRunStatusDto.setRun_status(TaskRunStatusEnum.IDLE.getStatus());
//        if (!taskRunStatusService.setTaskRunStatus(taskUuid, taskRunStatusDto))
//            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
//
//        // 构造URL
//        String ip = "http://" + taskInfosDto.getAsset_ip() + ":8191";
//        String url = ip + "/nodes/manage/run-task?uuid={uuid}";
//
//        // 构造参数map
//        HashMap<String, String> map = new HashMap<>();
//        map.put("uuid", taskUuid);
//
//        // 向节点发送请求，并返回节点的响应结果
//        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
//        return responseEntity.getBody();
//    }

    public ResponseBean runProjectTask(ProjectPo projectPo) {
        ArrayList<ResponseBean> responseBeanList = new ArrayList();
        // 根据tasks字段解析出所有task的taskUuid
        String tasks = projectPo.getTasks();
        int timeMode = projectPo.getRun_time_mode();
        if (tasks != null && !tasks.equals("")) {
            JSONArray jsonArray = JSONArray.parseArray(tasks);
            ResponseBean responseBean = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String taskUuid = object.getString("uuid");
                if (ProjectRunTimeModeEnum.MODE_NOW.getRunTimeMode() == timeMode) {
                    responseBean = executeSingleTask(projectPo.getUuid(), taskUuid,
                            (String) httpServletRequest.getSession().getAttribute(Const.USER_UUID));
                } else {
                    int delayTime = 0;
                    if (ProjectRunTimeModeEnum.MODE_30MINS_LATER.getRunTimeMode() == timeMode) {
                        delayTime = 30 * 60 * 1000;
                    } else if (ProjectRunTimeModeEnum.MODE_1HOUR_LATER.getRunTimeMode() == timeMode) {
                        delayTime = 60 * 60 * 1000;
                    } else if (ProjectRunTimeModeEnum.MODE_1DAY_LATER.getRunTimeMode() == timeMode) {
                        delayTime = 24 * 60 * 60 * 1000;
                    }
                    java.util.Timer timer = new java.util.Timer(true);
                    TimerTask task = new TimerTask() {
                        public void run() {
                            final ResponseBean result = executeSingleTask(taskUuid, projectPo.getUuid(), "");
                        }
                    };
                    timer.schedule(task, delayTime);
                }
                responseBeanList.add(responseBean);
            }
            return responseHelper.success(responseBeanList);
        }
        return null;
    }

    public ResponseBean executeSingleTask(String projectUuid, String taskUuid, String userUuid) {
        // 根据任务UUID，获取任务DTO
        TaskInfosDto taskInfosDto = tasksMapper.getTaskDtoByUuid(taskUuid);
        if (taskInfosDto == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        // 设置任务为空闲状态
        TaskRunStatusDto taskRunStatusDto = new TaskRunStatusDto();
        taskRunStatusDto.setProject_uuid(projectUuid);
        taskRunStatusDto.setTask_uuid(taskUuid);
        taskRunStatusDto.setRun_status(TaskRunStatusEnum.IDLE.getStatus());
        if (!taskRunStatusService.setTaskRunStatus(taskRunStatusDto))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 构造URL
        String ip = "http://" + taskInfosDto.getAsset_ip() + ":8191";
        String url = ip + "/nodes/manage/run-project-task?project_uuid={project_uuid}&task_uuid={task_uuid}&user_uuid={user_uuid}";

        // 构造参数map
        HashMap<String, String> map = new HashMap<>();
        map.put("project_uuid", projectUuid);
        map.put("task_uuid", taskUuid);
        map.put("user_uuid", userUuid);
//        map.put("user_uuid", (String)httpServletRequest.getSession().getAttribute(Const.USER_UUID));

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
        return responseEntity.getBody();
    }

    public ResponseBean checkTaskNameExist(String taskName, String taskUuid) {
        int count;

        if ((taskUuid == null) || (taskUuid.isEmpty()))
            count = tasksMapper.getTaskNameCount(taskName);
        else
            count = tasksMapper.checkNameInOtherTasks(taskName, taskUuid);

        JSONObject jsonData = new JSONObject();
        jsonData.put("task_name", taskName);
        jsonData.put("count", count);
        jsonData.put("exist", (count > 0) ? 1 : 0);

        return responseHelper.success(jsonData);
    }
}
