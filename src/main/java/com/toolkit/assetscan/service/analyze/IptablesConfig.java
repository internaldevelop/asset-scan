package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
public class IptablesConfig {
    @Autowired
    AnalyzeSubject resultOper;

    public boolean checkIptables(JSONObject iptables, JSONObject checkItems) {
        boolean active = iptables.getBooleanValue("active");
        if (!checkActive(active, checkItems)) {
            return false;
        }

        // 核查 Input chain 规则配置
        JSONArray inputChains = iptables.getJSONArray("Chain INPUT");
        if (!checkInputBlackList(inputChains, checkItems)) {
            return false;
        }
        if (!checkInputWhiteList(inputChains, checkItems)) {
            return false;
        }

        // 核查 Output chain 规则配置
        JSONArray outputChains = iptables.getJSONArray("Chain OUTPUT");
        if (!checkOutputBlackList(outputChains, checkItems)) {
            return false;
        }
        if (!checkOutputWhiteList(outputChains, checkItems)) {
            return false;
        }

        // 核查 Forward chain 规则配置
        JSONArray forwardChains = iptables.getJSONArray("Chain FORWARD");
        if (!checkForwardBlackList(forwardChains, checkItems)) {
            return false;
        }
        if (!checkForwardWhiteList(forwardChains, checkItems)) {
            return false;
        }

        return true;
    }

    private boolean checkActive(boolean active, JSONObject checkItems) {
        resultOper.setConfigType("iptables");

        // 检查消息日志的配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_ACTIVE)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_ACTIVE);
            resultOper.setConfigInfo("iptables服务：" + (active ? "已激活" : "未启动"));

            if (active) {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统已启动iptables服务，具备了网络防火墙配置能力。");
                resultOper.setSolution("");
            } else {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("未启动iptables服务，系统的网络访问控制存在威胁。");
                resultOper.setSolution("系统管理员安装iptables服务，并激活该服务为开机启动。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    private boolean checkInputBlackList(JSONArray inputChains, JSONObject checkItems) {
        // 检查接入黑名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_INPUT_BLACKLIST))
            return true;

        // 获取黑名单
        String blackList = blackList(inputChains);

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_INPUT_BLACKLIST);
        resultOper.setConfigInfo("黑名单：" + blackList);

        if (blackList.isEmpty()) {
            resultOper.setRiskLevel(2);
            resultOper.setRiskDesc("iptables未配置接入黑名单，难以有效防范未知网络威胁。");
            resultOper.setSolution("建议对iptables的INPUT链设置所有来源所有协议的REJECT规则，例子如下：" +
                    "-A INPUT -j REJECT --reject-with icmp-host-prohibited。");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置接入黑名单。" + blackList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private boolean checkInputWhiteList(JSONArray inputChains, JSONObject checkItems) {
        // 检查接入白名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_INPUT_WHITELIST))
            return true;

        // 获取白名单
        String whiteList = whiteList(inputChains);

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_INPUT_WHITELIST);
        resultOper.setConfigInfo("白名单：" + whiteList);

        if (whiteList.isEmpty()) {
            resultOper.setRiskLevel(1);
            resultOper.setRiskDesc("iptables未配置接入白名单，请检查iptables是否按需要配置。");
            resultOper.setSolution("建议对iptables的INPUT链设置指定来源指定协议的ACCEPT规则，例子如下：" +
                    " -I INPUT 1 -s 192.168.1.13 -p tcp --dport 22 -j ACCEPT。");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置接入白名单。" + whiteList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private boolean checkOutputBlackList(JSONArray outputChains, JSONObject checkItems) {
        // 检查外接黑名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_OUTPUT_BLACKLIST))
            return true;

        // 获取黑名单
        String blackList = getConfigInfo(outputChains, "DROP");

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_OUTPUT_BLACKLIST);
        resultOper.setConfigInfo("黑名单：" + blackList);

        if (blackList.isEmpty()) {
            resultOper.setRiskLevel(2);
            resultOper.setRiskDesc("iptables未配置外接黑名单，难以有效防范恶意程序的非法连接。");
            resultOper.setSolution("建议对iptables的OUTPUT链设置所有目标所有协议的REJECT规则，例子如下：" +
                    "-A OUTPUT -j REJECT --reject-with icmp-host-prohibited。");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置外部连接黑名单。" + blackList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private boolean checkOutputWhiteList(JSONArray outputChains, JSONObject checkItems) {
        // 检查外接白名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_OUTPUT_WHITELIST))
            return true;

        // 获取白名单
        String whiteList = whiteList(outputChains);

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_OUTPUT_WHITELIST);
        resultOper.setConfigInfo("白名单：" + whiteList);

        if (whiteList.isEmpty()) {
            resultOper.setRiskLevel(1);
            resultOper.setRiskDesc("iptables未配置外接白名单，请检查iptables是否按需要配置。");
            resultOper.setSolution("建议对iptables的OUTPUT链设置指定来源指定协议的ACCEPT规则，例子如下：" +
                    "-I INPUT 1 -s 192.168.1.13 -p tcp --dport 22 -j ACCEPT");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置外部连接白名单。" + whiteList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private boolean checkForwardBlackList(JSONArray fwdChains, JSONObject checkItems) {
        // 检查转发黑名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_FWD_BLACKLIST))
            return true;

        // 获取黑名单
        String blackList = blackList(fwdChains);

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_FWD_BLACKLIST);
        resultOper.setConfigInfo("黑名单：" + blackList);

        if (blackList.isEmpty()) {
            resultOper.setRiskLevel(2);
            resultOper.setRiskDesc("iptables未配置转发黑名单，难以有效防范非法转发连接。");
            resultOper.setSolution("建议对iptables的FORWARD链设置所有目标所有协议的REJECT规则，例子如下：" +
                    "-A FORWARD -j REJECT --reject-with icmp-host-prohibited。");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置转发黑名单。" + blackList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private boolean checkForwardWhiteList(JSONArray fwdChains, JSONObject checkItems) {
        // 检查转发白名单配置
        if (!resultOper.needCheck(checkItems, BaseLineItemEnum.IPT_FWD_WHITELIST))
            return true;

        // 获取白名单
        String whiteList = whiteList(fwdChains);

        resultOper.setConfigType("iptables");
        resultOper.saveCheckItem(checkItems, BaseLineItemEnum.IPT_FWD_WHITELIST);
        resultOper.setConfigInfo("白名单：" + whiteList);

        if (whiteList.isEmpty()) {
            resultOper.setRiskLevel(1);
            resultOper.setRiskDesc("iptables未配置转发白名单，请检查iptables是否按需要配置。");
            resultOper.setSolution("建议对iptables的FORWARD链设置指定来源指定协议的ACCEPT规则，例子如下：" +
                    "-A FORWARD -d 192.168.0.1 -m limit --limit 50/s -j ACCEPT");
        } else {
            resultOper.setRiskLevel(0);
            resultOper.setRiskDesc("iptables已配置转发白名单。" + whiteList);
            resultOper.setSolution("");
        }

        // 保存核查记录
        if (!resultOper.saveCheckResult())
            return false;
        return true;
    }

    private String blackList(JSONArray rules) {
        return getConfigInfo(rules, "REJECT");
    }

    private String whiteList(JSONArray rules) {
        return getConfigInfo(rules, "ACCEPT");
    }

    private String getConfigInfo(JSONArray rules, String target) {
        String config = "";
        if (!target.equals("REJECT") && !target.equals("DROP") && !target.equals("ACCEPT")) {
            return "";
        }

        for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
            JSONObject rule = (JSONObject) iter.next();
            // 来源
            String source = rule.getString("source");
            if (source.equals("0.0.0.0/0")) {
                source = "所有";
            }
            config += "源IP（" + source + "），";

            // 目标
            String dest = rule.getString("destination");
            if (dest.equals("0.0.0.0/0")) {
                dest = "所有";
            }
            config += "目标IP（" + dest + "），";

            // 协议
            String protocol = rule.getString("protocol");
            if (protocol.equals("all")) {
                protocol = "tcp/udp/icmp";
            }
            config += "协议（" + protocol + "），";

            // 端口
            String port = rule.getString("port");
            if (port == null || port.isEmpty()) {
                String portStart = rule.getString("portStart");
                String portEnd = rule.getString("portEnd");
                if (portStart != null && !portStart.isEmpty() && portEnd != null && !portEnd.isEmpty()) {
                    port = portStart + "--" + portEnd;
                } else {
                    port = "未配置";
                }
            }
            config += "端口（" + port + "）；";
        }

        return config;
    }
}
