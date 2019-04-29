package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.global.common.VerifyUtil;
import com.toolkit.assetscan.global.redis.IRedisClient;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.TaskExecuteResultsManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    @ApiImplicitParam(name = "taskNameIpType", value = "任务名称、目标IP、问题类型", required = true, dataType = "String",paramType="query")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody Object getAllTasks(String taskNameIpType) {

        return taskExecuteResultsManageService.getAllTasksResults(taskNameIpType);
    }

    /**
     * 5.2 任务检测结果(策略系统数量) 统计图表数据获取
     * @return : 任务检测结果(策略系统数量) 统计图表数据获取
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsStatistics() {
        return taskExecuteResultsManageService.getResultsStatistics();
    }

    /**
     * 5.3 任务检测结果(策略数量) 统计图表数据获取
     * @return : 任务检测结果(策略数量) 统计图表数据获取
     */
    @RequestMapping(value = "/policie-statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsPolicieStatistics() {
        return taskExecuteResultsManageService.getResultsPolicieStatistics();
    }

    /**
     * 5.4 任务检测结果(系统数量) 统计图表数据获取
     * @return : 任务检测结果(系统数量) 统计图表数据获取
     */
    @RequestMapping(value = "/sys-statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsSysStatistics() {
        return taskExecuteResultsManageService.getResultsSysStatistics();
    }

    /**
     * 5.9 获取IEE漏洞数（测试第二数据源）
     * @return
     */
    @RequestMapping(value = "/all-iie-vul", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllIieVulInfo() {
        return taskExecuteResultsManageService.getAllIieVulInfo();
    }



}
