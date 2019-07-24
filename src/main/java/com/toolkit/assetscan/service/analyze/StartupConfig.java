package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.html.HTMLDocument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
        resultOper.setConfigType("startup");

        // 检查SELinux的状态
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SELINUX_STATUS)) {
            boolean status = seLinux.getBooleanValue("status");
            resultOper.setCheckItem(resultOper.getCheckItemDesc(checkItems, BaseLineItemEnum.SELINUX_STATUS));
            resultOper.setConfigInfo("SELinux状态: " + status);
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
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SELINUX_MODE)) {
            String modeName = seLinux.getString("modeName");
            resultOper.setCheckItem(resultOper.getCheckItemDesc(checkItems, BaseLineItemEnum.SELINUX_MODE));
            resultOper.setConfigInfo("SELinux模式: " + modeName);
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
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SELINUX_POLICY)) {
            String policyName = seLinux.getString("policyName");
            resultOper.setCheckItem(resultOper.getCheckItemDesc(checkItems, BaseLineItemEnum.SELINUX_POLICY));
            resultOper.setConfigInfo("SELinux策略: " + policyName);
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
        resultOper.setConfigType("startup");
        String configInfo;

        // 检查自启动服务中是否包含防火墙服务
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.AUTORUN_FIREWALL)) {
            boolean firewallServiceEnable = false;
            configInfo = "未开启";
            for (Iterator iter = services.iterator(); iter.hasNext(); ) {
                JSONObject service = (JSONObject) iter.next();
                String serviceName = service.getString("service");
                if (serviceName.equals("firewalld") || serviceName.equals("iptables")) {
                    firewallServiceEnable = true;
                    configInfo = serviceName + " 运行级别 [" + service.getString("runLevel") + "]";
                    break;
                }
            }
            // 设置检查项：开机启动防火墙服务
            resultOper.setCheckItem(resultOper.getCheckItemDesc(checkItems, BaseLineItemEnum.AUTORUN_FIREWALL));
            resultOper.setConfigInfo("防火墙服务: " + configInfo);
            if (firewallServiceEnable) {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统已设置开机启动防火墙服务。");
                resultOper.setSolution("");
            } else {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("系统启动后没有防火墙服务。");
                resultOper.setSolution("建议安装 iptables-services，并将服务激活（systemctl enable iptables）；替代方案可激活 firewalld 服务（systemctl enable firewalld）。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        // 检查自启动服务中是否有不安全的服务
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.AUTORUN_EXCLUDED)) {
            String excludeServices = "lpd,telnet,routed,sendmail,Bluetooth,identd,xfs,rlogin,rwho,rsh,rexec,inetd,xinetd,daytime,chargen,echo";
            List<String> excludeList = Arrays.asList(excludeServices.split(","));
            List<String> riskServiceList = new ArrayList<>();
            boolean hasRiskService = false;
            configInfo = "";
            for (Iterator iter = services.iterator(); iter.hasNext(); ) {
                JSONObject service = (JSONObject) iter.next();
                String serviceName = service.getString("service");
                if (excludeList.contains(serviceName)) {
                    hasRiskService = true;
                    configInfo += serviceName + " 运行级别 [" + service.getString("runLevel") + "]；";
                    riskServiceList.add(serviceName);
                }
            }
            // 设置检查项：自启动服务的风险
            resultOper.setCheckItem(resultOper.getCheckItemDesc(checkItems, BaseLineItemEnum.AUTORUN_EXCLUDED));
            if (hasRiskService) {
                resultOper.setConfigInfo("安全风险服务: " + configInfo);
                resultOper.setRiskLevel(3);
                String riskServices = String.join(" | ", riskServiceList);
                resultOper.setRiskDesc("系统启动了有安全风险的服务：" + riskServices);
                resultOper.setSolution("停止服务（systemctl stop [服务名称]）：" + riskServices + "。并使服务失效（systemctl disable [服务名称]）。");
            } else {
                resultOper.setConfigInfo("安全风险服务: 未发现");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("未发现系统存在有安全风险的开机启动服务。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }
}
