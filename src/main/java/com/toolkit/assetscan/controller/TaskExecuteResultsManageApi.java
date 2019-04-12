package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.TaskExecuteResultsManageService;
import com.toolkit.assetscan.service.UserManageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/tasks/results")
@Api(value = "05. 检测结果", tags = "05-Tasks results Manager API")
public class TaskExecuteResultsManageApi {
    private Logger logger = LoggerFactory.getLogger(TaskExecuteResultsManageApi.class);
    private final TaskExecuteResultsManageService taskExecuteResultsManageService;
    private final ResponseHelper responseHelper;

    @Autowired
    public TaskExecuteResultsManageApi(TaskExecuteResultsManageService taskExecuteResultsManageService, ResponseHelper responseHelper) {
        this.taskExecuteResultsManageService = taskExecuteResultsManageService;
        this.responseHelper = responseHelper;
    }

    /**
     * 5.1 任务检测结果查询
     * @return payload: 所有任务的数组 （JSON 格式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllTasks() {
        return taskExecuteResultsManageService.getAllTasksResults();
    }

    /**
     * 5.2 任务检测结果 统计图表数据获取
     * @return : 任务检测结果 统计图表数据获取
     */
    @RequestMapping(value = "/statistical", method = RequestMethod.GET)
    public @ResponseBody
    Object getTasksResultsStat() {

        return taskExecuteResultsManageService.getAllTasksResults();
    }



}
