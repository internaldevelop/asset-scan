package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.*;
import com.toolkit.assetscan.bean.po.TaskExecuteResultsPo;
import com.toolkit.assetscan.dao.loophole.IieVulInfoMapper;
import com.toolkit.assetscan.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.CheckParams;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.security.VerifyHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskExecuteResultsManageService {
    private ResponseBean responseBean;
    @Autowired TaskRunStatusService taskRunStatusService;
    @Autowired TasksMapper tasksMapper;
    @Autowired TaskExecuteResultsMapper taskExecuteResultsMapper;
//    private IieVulInfoMapper iieVulInfoMapper;
    private final ResponseHelper responseHelper;
    private final CheckParams checkParams;
    private final VerifyHelper verifyHelper;

    public TaskExecuteResultsManageService(ResponseHelper responseHelper, CheckParams checkParams, VerifyHelper verifyHelper) {
        this.responseHelper = responseHelper;
        this.checkParams = checkParams;
        this.verifyHelper = verifyHelper;
    }

    /**
     * 获取所有任务检测结果
     * @return: 获取所有任务检测结果
     */
    public ResponseBean getAllTasksResults(String taskNameIpType) {
        List<TaskResultsDto> tasksList = taskExecuteResultsMapper.allTaskResults(taskNameIpType);
        if ( (tasksList == null) || (tasksList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(tasksList);
    }

    /**
     * 任务结果统计
     * @return
     */
    public Object getResultsStatistics() {
        List<TaskResultsStatisticsDto> tasksList = taskExecuteResultsMapper.getResultsStatistics();
        if ( (tasksList == null) || (tasksList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(tasksList);
    }

    /**
     * 任务统计（策略数量）
     * @return
     */
    public Object getResultsPolicieStatistics() {
        List<TaskResultsStatisticsDto> tasksList = taskExecuteResultsMapper.getResultsPolicieStatistics();
        if ( (tasksList == null) || (tasksList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(tasksList);
    }

    /**
     * 任务统计(系统数量)
     * @return
     */
    public Object getResultsSysStatistics() {
        List<TaskResultsStatisticsDto> tasksList = taskExecuteResultsMapper.getResultsSysStatistics();
        if ( (tasksList == null) || (tasksList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(tasksList);
    }

    /**
     * 执行状态信息和执行统计数据
     * @param taskUuid
     * @return
     */
    public Object getTaskExecBriefInfo(String taskUuid) {
        // 提取任务运行状态信息
        TaskRunStatusDto runStatus = taskRunStatusService.getTaskRunStatus(taskUuid);
        if (runStatus == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_RUN_STATUS_NOT_FOUND);

        // 获取任务信息
        TaskInfosDto taskInfo = tasksMapper.getTaskDtoByUuid(taskUuid);
        if (taskInfo == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_INFO_NOT_FOUND);

        // 获取任务指定运行（任务状态指定，即最新执行）的执行结果
        List<TaskExecuteResultsPo> taskExecResultsList = taskExecuteResultsMapper.getTaskExecBriefByExecUuid(runStatus.getExecute_uuid());
        if (taskExecResultsList == null || taskExecResultsList.size() == 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_INFO_NOT_FOUND);

        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("run_status", runStatus);
        jsonPayload.put("task_info", taskInfo);
        jsonPayload.put("exec_brief", taskExecResultsList);

        return responseHelper.success(jsonPayload);
    }

    public Object getResultRisksInfo(String execUuid, int riskLevel) {
        List<ExecRiskInfoDto> execRiskInfoDtoList = taskExecuteResultsMapper.getRiskInfo(execUuid, riskLevel);
        if (execRiskInfoDtoList == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_INFO_NOT_FOUND);
        return responseHelper.success(execRiskInfoDtoList);
    }

    /**
     * 获取IIE漏洞数（测试）
     * @return
     */
    public Object getAllIieVulInfo() {
//        int iieVulNum = iieVulInfoMapper.getIieVulNum();
//
//        Map<String, Object> retMap = new HashMap<>();
//        retMap.put("iie_vul_num", iieVulNum);
//
//        return responseHelper.success(retMap);
        return responseHelper.error(ErrorCodeEnum.ERROR_NOT_IMPLEMENTED);
    }

    public List<TaskResultsDto> getTasksResultsList(String taskNameIpType) {
        List<TaskResultsDto> tasksList = taskExecuteResultsMapper.allTaskResults(taskNameIpType);
        return tasksList;
    }

}
