package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.SystemConfigPo;
import com.toolkit.assetscan.dao.mybatis.SystemConfigsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemConfigService {
    private ResponseBean responseBean;
    private final SystemConfigsMapper systemConfigsMapper;
    private final ResponseHelper responseHelper;

    public SystemConfigService(SystemConfigsMapper systemConfigsMapper, ResponseHelper responseHelper) {
        this.systemConfigsMapper = systemConfigsMapper;
        this.responseHelper = responseHelper;
    }

    /**
     * 获取所有系统配置
     * @return payload: 所有系统配置的记录
     */
    public ResponseBean getAllSystemConfigs() {
        List<SystemConfigPo> systemConfigsList = systemConfigsMapper.getAllSystemConfigs();
        return responseHelper.success(systemConfigsList);
    }

    private ResponseBean successReturnSystemConfig(String name, String value) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", name);
        jsonData.put("value", value);
        return responseHelper.success(jsonData);
    }

    public ResponseBean addSystemConfig(SystemConfigPo systemConfigPo) {
        // 添加新系统配置记录
        if ( systemConfigsMapper.addSystemConfig(systemConfigPo) <= 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnSystemConfig(systemConfigPo.getName(), systemConfigPo.getValue());
    }

    /**
     * 根据name 更新系统配置
     * @param systemConfigPo
     * @return
     */
    public ResponseBean updateSystemConfig(SystemConfigPo systemConfigPo) {
        if (systemConfigsMapper.updateSystemConfig(systemConfigPo) <= 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnSystemConfig(systemConfigPo.getName(), systemConfigPo.getValue());
    }

    public ResponseBean deleteSystemConfig(String name) {
        if (systemConfigsMapper.deleteSystemConfig(name) <= 0) {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        return successReturnSystemConfig(name, null);
    }

    public ResponseBean getSystemConfigByName(String name) {
        SystemConfigPo systemConfigPo = systemConfigsMapper.getSystemConfigByName(name);
        return responseHelper.success(systemConfigPo);
    }
}
