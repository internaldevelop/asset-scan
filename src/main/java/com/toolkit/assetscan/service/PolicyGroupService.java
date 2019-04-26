package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.PolicyGroupPo;
import com.toolkit.assetscan.dao.mybatis.PolicyGroupsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyGroupService {
    private final PolicyGroupsMapper policyGroupsMapper;
    private final ResponseHelper responseHelper;

    public PolicyGroupService(PolicyGroupsMapper policyGroupsMapper, ResponseHelper responseHelper) {
        this.policyGroupsMapper = policyGroupsMapper;
        this.responseHelper = responseHelper;
    }

    public ResponseBean getAllGroups() {
        List<PolicyGroupPo> groupPropsList = policyGroupsMapper.allGroups();
        if ( (groupPropsList == null) || (groupPropsList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);

        return responseHelper.success(groupPropsList);
    }

    public ResponseBean addGroup(PolicyGroupPo groupProps) {
        // 为新策略随机分配一个UUID
        groupProps.setUuid(MyUtils.generateUuid());

        // 在字典表中增加一个分组记录，成功后返回分组名称和 UUID
        int rv = policyGroupsMapper.addGroup(groupProps);
        if (rv >= 1) {
            JSONObject jsonData = new JSONObject();
            jsonData.put("name", groupProps.getName());
            jsonData.put("uuid", groupProps.getUuid());
            return responseHelper.success(jsonData);
        }  else {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }
    }

    /**
     * 根据id获取策略组
     * @param groupId
     * @return
     */
    public ResponseBean getPolicyGroup(int groupId) {
        PolicyGroupPo group = policyGroupsMapper.getGroupById(groupId);
        if (group == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_GROUP_NOT_FOUND);
        }

        return responseHelper.success(group);
    }
}
