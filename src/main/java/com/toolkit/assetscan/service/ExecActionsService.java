package com.toolkit.assetscan.service;

import com.toolkit.assetscan.bean.dto.ExecActionsInfoDto;
import com.toolkit.assetscan.dao.mybatis.TaskExecActionsMapper;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecActionsService {
    @Autowired private TaskExecActionsMapper execActionsMapper;
    @Autowired private ResponseHelper responseHelper;

    public Object queryAllExecActions() {
        List<ExecActionsInfoDto> execActionsInfoDtoList = execActionsMapper.queryAllExecActionInfos();
        if (execActionsInfoDtoList == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_NO_EXEC_ACTIONS);

        return responseHelper.success(execActionsInfoDtoList);
    }
}
