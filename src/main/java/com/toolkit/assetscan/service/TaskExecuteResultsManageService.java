package com.toolkit.assetscan.service;

import com.toolkit.assetscan.bean.TaskExecuteResultsProps;
import com.toolkit.assetscan.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.CheckParams;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.security.VerifyHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskExecuteResultsManageService {
    private ResponseBean responseBean;
    private final TaskExecuteResultsMapper taskExecuteResultsMapper;
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
        List<TaskExecuteResultsProps> tasksList = taskExecuteResultsMapper.allTaskResults();
        if ( (tasksList == null) || (tasksList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_TASK_NOT_FOUND);

        return responseHelper.success(tasksList);
    }
}
