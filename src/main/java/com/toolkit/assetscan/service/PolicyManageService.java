package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.PolicyDetailInfoDto;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.po.PolicyGroupPo;
import com.toolkit.assetscan.bean.po.PolicyPo;
import com.toolkit.assetscan.dao.helper.PoliciesManageHelper;
import com.toolkit.assetscan.dao.mybatis.PoliciesMapper;
import com.toolkit.assetscan.dao.mybatis.PolicyGroupsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.GeneralStatusEnum;
import com.toolkit.assetscan.global.enumeration.ReportEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolicyManageService {
    private ResponseBean responseBean;
    private final ResponseHelper responseHelper;
    private final PoliciesManageHelper policiesManageHelper;
    private final PoliciesMapper policiesMapper;
    private final PolicyGroupsMapper policyGroupsMapper;

    public PolicyManageService(ResponseHelper responseHelper, PoliciesManageHelper policiesManageHelper, PoliciesMapper policiesMapper, PolicyGroupsMapper policyGroupsMapper) {
        this.responseHelper = responseHelper;
        this.policiesManageHelper = policiesManageHelper;
        this.policiesMapper = policiesMapper;
        this.policyGroupsMapper = policyGroupsMapper;
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

        policyPo.setStatus(GeneralStatusEnum.VALID.getStatus());
        // 目前OS类型和基线保持一致
        policyPo.setBaseline(policyPo.getOs_type());

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

    public ResponseBean getAllPolicies(boolean briefInfo) {
        List<PolicyPo> policiesList = null;
        if (briefInfo) {
            policiesList = policiesMapper.allPoliciesBrief();
        } else {
            policiesList = policiesMapper.allPolicies();
        }
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

    public ResponseBean getAllPolicyDetailInfos() {
        List<PolicyDetailInfoDto> policiesList = policiesMapper.getAllPolicyDetailInfos();
        return responseHelper.success(policiesList);
    }

    public ResponseBean updatePolicy(PolicyPo policyPo) {
        policyPo.setStatus(GeneralStatusEnum.VALID.getStatus());
        // 目前OS类型和基线保持一致
        policyPo.setBaseline(policyPo.getOs_type());
        if (!policiesManageHelper.updatePolicy(policyPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        return successReturnInfo( policyPo.getName(), policyPo.getCode(), policyPo.getUuid() );
    }

    public Object statisticsReport(String code) {
        List patchList = new ArrayList();

        if (ReportEnum.GROUP_WINDOWS_PATCH_INSTALL.value().equals(code) || ReportEnum.GROUP_LINUX_PATCH_INSTALL.value().equals(code)) {  // 补丁安装情况
            patchList = policiesMapper.patchNotInstalledReport();
        } else if (ReportEnum.GROUP_WINDOWS_SERVICES.value().equals(code) || ReportEnum.GROUP_LINUX_SERVICES.value().equals(code)) {  // 系统服务分析
            patchList = policiesMapper.systemServiceReport();
        } else if (ReportEnum.GROUP_WINDOWS_FILE_SECURITY.value().equals(code) || ReportEnum.GROUP_LINUX_FILE_SECURITY.value().equals(code)) {  // 系统文件安全防护分析
            patchList = policiesMapper.systemFileServiceReport();
        } else if (ReportEnum.GROUP_WINDOWS_USER_ACCOUNT_CONFIGURATION.value().equals(code) || ReportEnum.GROUP_LINUX_USER_ACCOUNT_CONFIGURATION.value().equals(code)) {  // 用户账号配置分析
            patchList = policiesMapper.userAccountReport();
        } else if (ReportEnum.GROUP_WINDOWS_PASSWORD_CONFIGURATION.value().equals(code) || ReportEnum.GROUP_LINUX_PASSWORD_CONFIGURATION.value().equals(code)) {  // 口令策略配置分析
            patchList = policiesMapper.pwdPolicyReport();
        } else if (ReportEnum.GROUP_WINDOWS_NETWORK_COMMUNICATION_CONFIGURATION.value().equals(code) || ReportEnum.GROUP_LINUX_NETWORK_COMMUNICATION_CONFIGURATION.value().equals(code)) {  // 网络通信配置分析
            patchList = policiesMapper.networkReport();
        } else if (ReportEnum.GROUP_WINDOWS_LOG_AUDIT_CONFIGURATION.value().equals(code) || ReportEnum.GROUP_LINUX_LOG_AUDIT_CONFIGURATION.value().equals(code)) {  // 日志审计分析
            patchList = policiesMapper.logReport();
        } // 还没有添加防火墙相关的

        /*if ( patchList.size() == 0 ) {
            return responseHelper.error(ErrorCodeEnum.ERROR_NOT_DATA);
        }*/
        return responseHelper.success(patchList);

    }

    public ResponseBean statisticsPoliciesByGroup(String policyGroupId) {
        PolicyGroupPo group = policyGroupsMapper.getGroupById(1);
        if (group == null) {
            return null;
        }
        String policyGroupUuid = group.getUuid();
        if (StringUtils.isEmpty(policyGroupUuid)) {
            return null;
        }
//        List<PolicyPo> policiesList = policiesMapper.getPoliciesByGroup(policyGroupUuid);
//        return responseHelper.success(policiesList);
        return null;
    }


}
