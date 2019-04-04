package com.toolkit.assetscan.dao.helper;

import com.toolkit.assetscan.bean.PolicyProps;
import com.toolkit.assetscan.dao.mybatis.PoliciesMapper;
import com.toolkit.assetscan.global.enumeration.DeleteModeEnum;
import com.toolkit.assetscan.global.enumeration.PolicyStatusEnum;
import org.springframework.stereotype.Component;

@Component
public class PoliciesManageHelper {
    // 1 表示永久删除，2 表示逻辑删除
    private DeleteModeEnum DELETE_MODE = DeleteModeEnum.PERMANENT;
    private final PoliciesMapper policiesMapper;

    public PoliciesManageHelper(PoliciesMapper policiesMapper) {
        this.policiesMapper = policiesMapper;
    }

    public boolean addPolicy(PolicyProps policyProps) {
        int rv = policiesMapper.addPolicy(policyProps);
        return (rv > 0);
    }

    public boolean updatePolicy(PolicyProps policyProps) {
        int rv = policiesMapper.updatePolicy(policyProps);
        return (rv > 0);
    }

    public boolean deletePolicy(String policyUuid) {
        int rv = 0;
        if (DELETE_MODE == DeleteModeEnum.PERMANENT) {
            rv = policiesMapper.deletePolicy(policyUuid);
        } else if (DELETE_MODE == DeleteModeEnum.LOGICAL) {
            rv = policiesMapper.updateStatus(policyUuid, PolicyStatusEnum.LOGICAL_DELETE.getStatus());
        }

        return (rv > 0);
    }

    public boolean updatePolicyStatus(String policyUuid) {
        int rv = policiesMapper.updateStatus(policyUuid, PolicyStatusEnum.LOGICAL_DELETE.getStatus());
        return (rv > 0);
    }
}
