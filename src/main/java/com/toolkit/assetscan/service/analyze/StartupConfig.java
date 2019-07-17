package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig {
    @Autowired
    AnalyzeSubject resultOper;

    /**
     * baseline 格式如下：
     * {
     *     startup: {
     *         SELinux_status: "检查SELinux是否开启",
     *         SELinux_mode: "检查SELinux模式是否为enforcing",
     *         SELinux_policy: "检查SELinux策略是否为strict",
     *     }
     * }
     * @param seLinux
     * @param checkItems
     * @return
     */
    public boolean checkSELinux(JSONObject seLinux, JSONObject checkItems) {
        resultOper.setConfigType("开机安全配置");
        int riskLevel;
        String riskDesc;
        String solution;
        String configInfo;

        // 检查SELinux的状态
        if (checkItems.getString("SELinux_status") != null) {
            boolean status = seLinux.getBooleanValue("status");
            resultOper.setCheckItem(checkItems.getString("SELinux_status"));
            resultOper.setConfigInfo("SELinux_status: " + status);
            if (status == true) {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("被核查资产已开启强制访问控制安全系统（SELinux）。");
                resultOper.setSolution("");
            } else {
                resultOper.setRiskLevel(3);
                resultOper.setRiskDesc("被核查资产已开启强制访问控制安全系统（SELinux），系统存在严重的安全风险。");
                resultOper.setSolution("打开/etc/sysconfig/selinux，设置 SELINUX=enforcing 或者 SELINUX=permissive，重启操作系统以生效。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        // 检查SELinux的模式
        if (checkItems.getString("SELinux_mode") != null) {
            String modeName = seLinux.getString("modeName");
            resultOper.setCheckItem(checkItems.getString("SELinux_mode"));
            resultOper.setConfigInfo("SELinux_mode: " + modeName);
            if (modeName == "enforcing") {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("资产系统的SELinux模式已设置为强制模式。");
                resultOper.setSolution("");
            } else {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("资产系统中的SELinux模式不是强制模式，有安全风险。");
                resultOper.setSolution("打开/etc/sysconfig/selinux文件，设置 SELINUX=enforcing，重启操作系统以生效。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        // 检查SELinux的保护类型
        if (checkItems.getString("SELinux_policy") != null) {
            String policyName = seLinux.getString("policyName");
            resultOper.setCheckItem(checkItems.getString("SELinux_policy"));
            resultOper.setConfigInfo("SELinux_policy: " + policyName);
            if (policyName == "strict") {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("资产系统的SELinux策略已设置为对整个系统进行保护。");
                resultOper.setSolution("");
            } else {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("被核查系统的SELinux策略为部分保护，建议增强保护策略。");
                resultOper.setSolution("打开/etc/sysconfig/selinux文件，设置 SELINUXTYPE=strict，重启操作系统以生效。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    /**
     * baseline 格式如下：
     * {
     *     startup: {
     *         StartService_included: "推荐包含自启动服务",
     *         StartService_excluded: "排除不安全的自启动服务",
     *     }
     * }
     * @param services
     * @param checkItems
     * @return
     */
    public boolean checkSelfRunServices(JSONArray services, JSONObject checkItems) {
        String configType = "开机安全配置";

        // 检查自启动服务中是否指定必须的服务
        if (checkItems.getString("StartService_included") != null) {

        }

        // 检查自启动服务中是否有不安全的服务
        if (checkItems.getString("StartService_excluded") != null) {

        }
        return true;
    }
}
