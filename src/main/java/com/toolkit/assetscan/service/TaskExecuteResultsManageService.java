package com.toolkit.assetscan.service;

import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.dto.TaskResultsStatisticsDto;
import com.toolkit.assetscan.dao.loophole.IieVulInfoMapper;
import com.toolkit.assetscan.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.CheckParams;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.security.VerifyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskExecuteResultsManageService {
    private ResponseBean responseBean;
    private final TaskExecuteResultsMapper taskExecuteResultsMapper;
    @Autowired
    private IieVulInfoMapper iieVulInfoMapper;
    private final ResponseHelper responseHelper;
    private final CheckParams checkParams;
    private final VerifyHelper verifyHelper;

    public TaskExecuteResultsManageService(TaskExecuteResultsMapper taskExecuteResultsMapper, ResponseHelper responseHelper, CheckParams checkParams, VerifyHelper verifyHelper) {
        this.taskExecuteResultsMapper = taskExecuteResultsMapper;
        this.responseHelper = responseHelper;
        this.checkParams = checkParams;
        this.verifyHelper = verifyHelper;
    }

    /**
     * 获取所有任务检测结果
     * @return: 获取所有任务检测结果
     */
    public ResponseBean getAllTasksResults() {
        List<TaskResultsDto> tasksList = taskExecuteResultsMapper.allTaskResults();
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

//        TaskResultsStatisticsDto retDto = new TaskResultsStatisticsDto();
//
//        List<String> sysType = new ArrayList<String>();  // 系统类型
//        sysType.add("product");
//
//        List<Map<String, Object>> policieTypeNum = new ArrayList<Map<String, Object>>();
//        List<String> policieNames = new ArrayList<String>();  // 策略
//        for (TaskResultsStatisticsDto trsDto : tasksList) {
//            String oType = trsDto.getOs_type();  // 系统名称
//            if (!sysType.contains(oType)) {
//                sysType.add(oType);
//            }
//
//            String pName = trsDto.getPolicie_name();  // 策略名称
//            if (!policieNames.contains(pName)) {
//                policieNames.add(pName);
//
//                Map<String, Object> pMap = new HashMap<>();
//                pMap.put("product", pName);
//                policieTypeNum.add(pMap);
//            }
//        }
//
//
//        for (TaskResultsStatisticsDto trsDto : tasksList) {
//            String polName = trsDto.getPolicie_name();
//
//
//        }
//
//        return retDto;
    }

    /**
     * 获取IIE漏洞数（测试）
     * @return
     */
    public Object getAllIieVulInfo() {
        int iieVulNum = iieVulInfoMapper.getIieVulNum();

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("iie_vul_num", iieVulNum);

        return responseHelper.success(retMap);
    }

}
