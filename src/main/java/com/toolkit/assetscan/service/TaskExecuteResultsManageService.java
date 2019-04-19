package com.toolkit.assetscan.service;

import com.toolkit.assetscan.dao.loophole.IieVulInfoMapper;
import com.toolkit.assetscan.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.CheckParams;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.security.VerifyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
