package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.bean.po.ProjectPo;
import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.TaskExecuteScheduler;
import com.toolkit.assetscan.service.TaskManageService;
import com.toolkit.assetscan.service.TaskRunStatusService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/tasks")
@Api(value = "03. 任务管理接口", tags = "03-Tasks Manager API")
public class TaskManageApi {
    private Logger logger = LoggerFactory.getLogger(TaskManageApi.class);

    private final TaskManageService taskManageService;
    private final ResponseHelper responseHelper;

    @Autowired
    private TaskRunStatusService taskRunStatusService;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @Autowired
    public TaskManageApi(TaskManageService taskManageService, ResponseHelper responseHelper) {
        this.taskManageService = taskManageService;
        this.responseHelper = responseHelper;
    }

    /**
     * 3.1 添加一个新的任务
     * @param task task 参数
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addTask(@ModelAttribute TaskPo task, BindingResult bindingResult) {
        return taskManageService.addTask(task);
    }

    /**
     * 3.2 移除一个任务
     * @param taskUuid 任务的 UUID
     * @return payload: 无
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    Object removeTask(@RequestParam("uuid") String taskUuid) {
        return taskManageService.deleteTask(taskUuid);
    }

    /**
     * 3.3 获取所有的任务
     * @return payload: 所有任务的数组 （JSON 格式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllTasks() {
        return taskManageService.getAllTasks();
    }

    /**
     * 3.4 根据指定的 UUID 获取一条任务参数
     * @param taskUuid 任务的 UUID
     * @return payload: 任务参数（任务记录及外键字段的含义）
     */
    @RequestMapping(value = "/get-task", method = RequestMethod.GET)
    public @ResponseBody
    Object getTask(@RequestParam("uuid") String taskUuid) {
        return taskManageService.getTaskByUuid(taskUuid);
    }

    /**
     * 3.5 更新一条记录
     * @param task 任务参数
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object  updateTask(@ModelAttribute TaskPo task) {
        return taskManageService.updateTask(task);
    }

    /**
     * 3.6 执行一条任务，将执行结果保存到数据库中，并通过消息队列通知前端应用
     * @param taskUuid 任务对象
     * @return payload: 执行结果的 UUID
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public @ResponseBody
    Object  executeTask(@RequestParam("uuid") String taskUuid) {
        return taskManageService.executeSingleTask(Const.DEFAULT_PROJ_UUID, taskUuid,
                (String)httpServletRequest.getSession().getAttribute(Const.USER_UUID));
    }

    /**
     * 3.7 获取所有任务资产信息
     * @return payload: 任务详细信息（含任务关联的创建用户、资产等信息）
     */
    @RequestMapping(value = "/all-task-details", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllTaskDetails() {
        return taskManageService.getAllTaskDetails();
    }

    /**
     * 3.8 新建任务的详细信息
     * @param taskInfosDto 任务 DTO 信息，含任务、资产和用户
     * @param bindingResult 表单信息绑定结果
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "add-task-details", method = RequestMethod.POST)
    public @ResponseBody
    Object addTaskDetails(@ModelAttribute TaskInfosDto taskInfosDto, BindingResult bindingResult) {
        return taskManageService.addTaskDetails(taskInfosDto);
    }

    /**
     * 3.9 更新任务的详细信息
     * @param taskInfosDto 任务 DTO 信息，含任务、资产和用户
     * @param bindingResult 表单信息绑定结果
     * @return payload: 任务名和任务的 UUID
     */
    @RequestMapping(value = "update-task-details", method = RequestMethod.POST)
    public @ResponseBody
    Object updateTaskDetails(@ModelAttribute TaskInfosDto taskInfosDto, BindingResult bindingResult) {
        return taskManageService.updateTaskDetails(taskInfosDto);
    }

    /**
     * 3.10 获取任务的运行状态信息
     * 如果下列两个参数都为空，则返回所有可查询到的任务运行状态信息
     * @param projectUuid 项目UUID，如果没有该请求参数，使用缺省项目 UUID
     * @param taskUuid 单个任务的 UUID
     * @param tasksUuidList 多个任务 UUID 的集合，用逗号 ',' 分隔的 UUID 字符串
     * @return
     */
    @RequestMapping(value = "run-status", method = RequestMethod.GET)
    public @ResponseBody
    Object queryTaskRunStatus(@RequestParam(value = "project_uuid", required = false) String projectUuid,
                              @RequestParam(value = "uuid", required = false) String taskUuid,
                              @RequestParam(value = "uuid_list", required = false) String tasksUuidList) {
        // 未指定 project_uuid ，则使用缺省项目 UUID
        if (projectUuid == null || projectUuid.isEmpty())
            projectUuid = Const.DEFAULT_PROJ_UUID;

        if (taskUuid != null && !taskUuid.isEmpty()) {
            // 获取单个任务的运行状态信息
            TaskRunStatusDto taskRunStatusDto = taskRunStatusService.getTaskRunStatus(taskUuid, projectUuid);
            if (taskRunStatusDto == null) {
                return responseHelper.error(ErrorCodeEnum.ERROR_TASK_RUN_STATUS_NOT_FOUND);
            }

            return responseHelper.success(taskRunStatusDto);

        } else if (tasksUuidList != null && !tasksUuidList.isEmpty()) {
            // 获取多个任务的运行状态信息
            List <TaskRunStatusDto> taskRunStatusDtoList = taskRunStatusService.getTasksListRunStatus(tasksUuidList, projectUuid);
            /*if (taskRunStatusDtoList == null || taskRunStatusDtoList.size() == 0)
                return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND.ERROR_TASK_RUN_STATUS_NOT_FOUND);*/

            return responseHelper.success(taskRunStatusDtoList);

        } else {
            // 获取所有可查询到任务的运行状态信息
            List <TaskRunStatusDto> taskRunStatusDtoList = taskRunStatusService.getAllTasksRunStatus(projectUuid);
            if (taskRunStatusDtoList == null || taskRunStatusDtoList.size() == 0)
                return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND.ERROR_TASK_RUN_STATUS_NOT_FOUND);

            return responseHelper.success(taskRunStatusDtoList);

        }
    }

    /**
     * 3.11 执行项目任务，将执行结果保存到数据库中，并通过消息队列通知前端应用
     * @param projectPo 项目参数
     * @return payload: 执行结果的 UUID
     */
    @RequestMapping(value = "/execute-project-task", method = RequestMethod.POST)
    public @ResponseBody
    Object  executeTask(@ModelAttribute ProjectPo projectPo) {
        return taskManageService.runProjectTask(projectPo);
    }

    @Autowired
    TaskExecuteScheduler taskExecuteScheduler;

    /**
     * 3.12 设置任务的定时计划
     * @param taskUuid
     * @param projectUuid
     * @param runTime  格式强制为 HH:mm:ss
     * @return
     */
    @RequestMapping(value = "/set-task-schedule", method = RequestMethod.GET)
    public @ResponseBody
    Object  setTaskExecuteSchedule(@RequestParam("task_uuid") String taskUuid,
                                   @RequestParam(value = "project_uuid", required = false) String projectUuid,
                                   @RequestParam("run_time") String runTime) {
        // 未指定 project_uuid ，则使用缺省项目 UUID
        if (projectUuid == null || projectUuid.isEmpty())
            projectUuid = Const.DEFAULT_PROJ_UUID;
        return taskExecuteScheduler.setTask(taskUuid, projectUuid,
                (String)httpServletRequest.getSession().getAttribute(Const.USER_UUID),
                runTime);
    }

    /**
     * 3.13 停止任务的定时计划
     * @param taskUuid
     * @param projectUuid
     * @return
     */
    @RequestMapping(value = "stop-scheduler", method = RequestMethod.GET)
    public @ResponseBody
    Object stopTaskScheduler(@RequestParam("task_uuid") String taskUuid,
                             @RequestParam(value = "project_uuid", required = false) String projectUuid) {
        // 未指定 project_uuid ，则使用缺省项目 UUID
        if (projectUuid == null || projectUuid.isEmpty())
            projectUuid = Const.DEFAULT_PROJ_UUID;
        return taskExecuteScheduler.stopTask(taskUuid, projectUuid);
    }

    /**
     * 3.14 根据指定的 UUID 获取一条任务详细参数
     * @param taskUuid 任务的 UUID
     * @return payload: 任务详细参数
     */
    @RequestMapping(value = "/get-taskinfo", method = RequestMethod.GET)
    public @ResponseBody
    Object getTaskInfo(@RequestParam("uuid") String taskUuid) {
        return taskManageService.getTaskInfoByUuid(taskUuid);
    }
}
