package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.BaseLinePo;
import com.toolkit.assetscan.dao.mybatis.BaseLineMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class BaseLineTemplatesService {
    @Autowired
    BaseLineMapper baseLineMapper;
    @Autowired
    ResponseHelper responseHelper;

    public ResponseBean queryBaseLines(int level) {
        List<BaseLinePo> baseLines;
        if (level <= 0) {
            baseLines = baseLineMapper.getAllBaseLines();
            if (baseLines == null)
                return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
        } else {
            baseLines = new ArrayList<>();
            BaseLinePo baseLinePo = baseLineMapper.getBaseLine(level);
            if (baseLinePo == null)
                return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
            baseLines.add(baseLinePo);
        }
        return responseHelper.success(baseLines);
    }

    public ResponseBean initBaselineTemplates() {
        // 构造一级基线模板
        if (!buildLevelOneTemplate()) {
            responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 构造二级基线模板
        if (!buildLevelTwoTemplate()) {
            responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 构造三级基线模板
        if (!buildLevelThreeTemplate()) {
            responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        return responseHelper.success();
    }

    private boolean buildLevelOneTemplate() {
        List<BaseLineItemEnum> items = new ArrayList<BaseLineItemEnum>(Arrays.asList(
                BaseLineItemEnum.SELINUX_STATUS,
                BaseLineItemEnum.AUTORUN_FIREWALL,
                BaseLineItemEnum.AUTORUN_EXCLUDED,
                BaseLineItemEnum.ACCOUNTS_REDUNDANT,
                BaseLineItemEnum.ACCOUNTS_PLAIN_PWD,
                BaseLineItemEnum.PWD_RETRY,
                BaseLineItemEnum.PWD_MINLEN,
                BaseLineItemEnum.PWD_DIFOK,
                BaseLineItemEnum.PWD_U_CREDIT,
                BaseLineItemEnum.SERVICE_IPTABLES,
                BaseLineItemEnum.SERVICE_SSH_DENYALL,
                BaseLineItemEnum.SERVICE_SSH_ALLOW,
                BaseLineItemEnum.SERVICE_SSH_ROOT_LOGIN,
                BaseLineItemEnum.LOGIN_DENY_COUNT,
                BaseLineItemEnum.LOGIN_UNLOCK_TIME,
                BaseLineItemEnum.LOG_MESSAGES,
                BaseLineItemEnum.LOG_SECURE,
                BaseLineItemEnum.LOG_EMERGENCY,
                BaseLineItemEnum.IPT_ACTIVE,
                BaseLineItemEnum.IPT_INPUT_BLACKLIST
        ));

        return buildTemplate(1, items);
    }

    private boolean buildLevelTwoTemplate() {
        List<BaseLineItemEnum> items = new ArrayList<BaseLineItemEnum>(Arrays.asList(
                BaseLineItemEnum.SELINUX_STATUS,
                BaseLineItemEnum.SELINUX_MODE,
                BaseLineItemEnum.SELINUX_POLICY,
                BaseLineItemEnum.AUTORUN_FIREWALL,
                BaseLineItemEnum.AUTORUN_EXCLUDED,
                BaseLineItemEnum.ACCOUNTS_USELESS,
                BaseLineItemEnum.ACCOUNTS_REDUNDANT,
                BaseLineItemEnum.ACCOUNTS_PLAIN_PWD,
                BaseLineItemEnum.ACCOUNTS_ROOT_NUM,
                BaseLineItemEnum.PWD_RETRY,
                BaseLineItemEnum.PWD_MINLEN,
                BaseLineItemEnum.PWD_AGE,
                BaseLineItemEnum.PWD_DIFOK,
                BaseLineItemEnum.PWD_U_CREDIT,
                BaseLineItemEnum.PWD_L_CREDIT,
                BaseLineItemEnum.SERVICE_IPTABLES,
                BaseLineItemEnum.SERVICE_SSH_DENYALL,
                BaseLineItemEnum.SERVICE_SSH_ALLOW,
                BaseLineItemEnum.SERVICE_SSH_ROOT_LOGIN,
                BaseLineItemEnum.LOGIN_DENY_ROOT,
                BaseLineItemEnum.LOGIN_DENY_COUNT,
                BaseLineItemEnum.LOGIN_UNLOCK_TIME,
                BaseLineItemEnum.LOGIN_ROOT_UNLOCK_TIME,
                BaseLineItemEnum.LOG_MESSAGES,
                BaseLineItemEnum.LOG_SECURE,
                BaseLineItemEnum.LOG_CRON,
                BaseLineItemEnum.LOG_EMERGENCY,
                BaseLineItemEnum.LOG_BOOT,
                BaseLineItemEnum.IPT_ACTIVE,
                BaseLineItemEnum.IPT_INPUT_BLACKLIST,
                BaseLineItemEnum.IPT_INPUT_WHITELIST,
                BaseLineItemEnum.IPT_FWD_WHITELIST
        ));

        return buildTemplate(2, items);
    }

    private boolean buildLevelThreeTemplate() {
        List<BaseLineItemEnum> items = new ArrayList<BaseLineItemEnum>(Arrays.asList(
                BaseLineItemEnum.SELINUX_STATUS,
                BaseLineItemEnum.SELINUX_MODE,
                BaseLineItemEnum.SELINUX_POLICY,
                BaseLineItemEnum.AUTORUN_FIREWALL,
                BaseLineItemEnum.AUTORUN_EXCLUDED,
                BaseLineItemEnum.ACCOUNTS_USELESS,
                BaseLineItemEnum.ACCOUNTS_REDUNDANT,
                BaseLineItemEnum.ACCOUNTS_PLAIN_PWD,
                BaseLineItemEnum.ACCOUNTS_ROOT_NUM,
                BaseLineItemEnum.GROUPS_PLAIN_PWD,
                BaseLineItemEnum.PWD_WARN_AGE,
                BaseLineItemEnum.PWD_RETRY,
                BaseLineItemEnum.PWD_MINLEN,
                BaseLineItemEnum.PWD_AGE,
                BaseLineItemEnum.PWD_DIFOK,
                BaseLineItemEnum.PWD_U_CREDIT,
                BaseLineItemEnum.PWD_L_CREDIT,
                BaseLineItemEnum.PWD_D_CREDIT,
                BaseLineItemEnum.SERVICE_FIREWALLD,
                BaseLineItemEnum.SERVICE_IPTABLES,
                BaseLineItemEnum.SERVICE_SSH_DENYALL,
                BaseLineItemEnum.SERVICE_SSH_ALLOW,
                BaseLineItemEnum.SERVICE_SSH_ROOT_LOGIN,
                BaseLineItemEnum.SERVICE_SSH_PORT,
                BaseLineItemEnum.LOGIN_DENY_ROOT,
                BaseLineItemEnum.LOGIN_DENY_COUNT,
                BaseLineItemEnum.LOGIN_UNLOCK_TIME,
                BaseLineItemEnum.LOGIN_ROOT_UNLOCK_TIME,
                BaseLineItemEnum.LOG_MESSAGES,
                BaseLineItemEnum.LOG_SECURE,
                BaseLineItemEnum.LOG_MAILLOG,
                BaseLineItemEnum.LOG_CRON,
                BaseLineItemEnum.LOG_EMERGENCY,
                BaseLineItemEnum.LOG_BOOT,
                BaseLineItemEnum.LOG_ROTATE,
                BaseLineItemEnum.IPT_ACTIVE,
                BaseLineItemEnum.IPT_INPUT_BLACKLIST,
                BaseLineItemEnum.IPT_INPUT_WHITELIST,
                BaseLineItemEnum.IPT_OUTPUT_BLACKLIST,
                BaseLineItemEnum.IPT_OUTPUT_WHITELIST,
                BaseLineItemEnum.IPT_FWD_BLACKLIST,
                BaseLineItemEnum.IPT_FWD_WHITELIST
        ));

        return buildTemplate(3, items);
    }

    private boolean buildTemplate(int level, List<BaseLineItemEnum> items) {
        JSONObject template = new JSONObject();
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            BaseLineItemEnum item = (BaseLineItemEnum) iter.next();
            // 查找策略所在组
            JSONObject group = template.getJSONObject(item.getGroup());
            // 未建组的新建 JSON 对象
            if (group == null) {
                group = new JSONObject();
                template.put(item.getGroup(), group);
            }

            // 组对象中保存策略
            group.put(item.getName(), item.getDesc());
        }

        // 更新数据库中的基线模板数据
        if (baseLineMapper.updateTemplate(level, template.toJSONString()) != 1) {
            return false;
        }

        return true;
    }
}
