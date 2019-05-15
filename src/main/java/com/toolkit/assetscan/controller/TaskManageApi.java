package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.TaskManageService;
import com.toolkit.assetscan.service.TaskRunStatusService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
     * @param taskUuid 任务的 UUID
     * @return payload: 执行结果的 UUID
     */
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public @ResponseBody
    Object  executeTask(@RequestParam("uuid") String taskUuid) {
        return taskManageService.runTask(taskUuid);
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
     * @param taskUuid
     * @return
     */
    @RequestMapping(value = "run-status", method = RequestMethod.GET)
    public @ResponseBody
    Object getTaskRunStatus(@RequestParam("uuid") String taskUuid) {
//        TaskRunStatusDto runStatusDto = taskRunStatusService.getTaskRunStatus("1111");
//        runStatusDto = new TaskRunStatusDto();
//        runStatusDto.setTotal_jobs_count(222);
//        taskRunStatusService.setTaskRunStatus("1111", runStatusDto);
//        runStatusDto = taskRunStatusService.getTaskRunStatus("1111");
//        String value = taskRunStatusService.getString("AAAA");
//        taskRunStatusService.setString("AAAA", "A2222");
//        value = taskRunStatusService.getString("AAAA");

        TaskRunStatusDto taskRunStatusDto = taskRunStatusService.getTaskRunStatus(taskUuid);
        if (taskRunStatusDto == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_RUN_STATUS_NOT_FOUND);
        }

        return responseHelper.success(taskRunStatusDto);
    }
}
