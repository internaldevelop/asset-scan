package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.PolicyPo;
import com.toolkit.assetscan.dao.helper.PoliciesManageHelper;
import com.toolkit.assetscan.dao.mybatis.PoliciesMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyManageService {
    private ResponseBean responseBean;
    private final ResponseHelper responseHelper;
    private final PoliciesManageHelper policiesManageHelper;
    private final PoliciesMapper policiesMapper;

    public PolicyManageService(ResponseHelper responseHelper, PoliciesManageHelper policiesManageHelper, PoliciesMapper policiesMapper) {
        this.responseHelper = responseHelper;
        this.policiesManageHelper = policiesManageHelper;
        this.policiesMapper = policiesMapper;
    }
    private boolean iCheckParams(PolicyPo policyPo) {
        responseBean = responseHelper.success();
        return true;
    }

    private ResponseBean successReturnInfo(String policyName, String code, String policyUuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", policyName);
        jsonData.put("code", code);
        jsonData.put("uuid", policyUuid);
        return responseHelper.success(jsonData);
    }

    public ResponseBean addPolicy(PolicyPo policyPo) {
        // 检查参数
        if (!iCheckParams(policyPo))
            return responseBean;

        // 为新策略随机分配一个UUID
        policyPo.setUuid(MyUtils.generateUuid());

        // 记录新策略的创建时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        policyPo.setCreate_time(currentTime);

        // 往数据库里写入这条新策略
        if (!policiesManageHelper.addPolicy(policyPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnInfo( policyPo.getName(), policyPo.getCode(), policyPo.getUuid() );
    }

    public ResponseBean removePolicy(String policyUuid) {
        // 获取策略数据，找不到则返回错误
        PolicyPo policyPo = policiesMapper.getPolicyByUuid(policyUuid);
        if (policyPo == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);
        }

        // 移除该策略
        if (!policiesManageHelper.deletePolicy(policyUuid)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 返回数据包含已删除策略的名称、代码和 UUID
        return successReturnInfo( policyPo.getName(), policyPo.getCode(), policyPo.getUuid() );
    }

    public ResponseBean getAllPolicies() {
        List<PolicyPo> policiesList = policiesMapper.allPolicies();
        if ( (policiesList == null) || (policiesList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);

        return responseHelper.success(policiesList);
    }

    public ResponseBean getPolicy(String policyUuid) {
        PolicyPo policy = policiesMapper.getPolicyByUuid(policyUuid);
        if (policy == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);
        }

        return responseHelper.success(policy);
    }

    public ResponseBean getPoliciesByGroupUuid(String policyGroupUuid) {
        List<PolicyPo> policiesList = policiesMapper.getPoliciesByGroupUuid(policyGroupUuid);
        return responseHelper.success(policiesList);
    }

    public ResponseBean getPoliciesByGroupCode(String policyGroupCode) {
        List<PolicyPo> policiesList = policiesMapper.getPoliciesByGroupCode(policyGroupCode);
        return responseHelper.success(policiesList);
    }

    public ResponseBean updatePolicy(PolicyPo policyPo) {
        if (!policiesManageHelper.updatePolicy(policyPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        return successReturnInfo( policyPo.getName(), policyPo.getCode(), policyPo.getUuid() );
    }
}
