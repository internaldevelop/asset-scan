package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig {
    @Autowired
    AnalyzeSubject resultOper;

    public boolean checkService(JSONObject payload, JSONObject checkItems) {
        if (!checkFirewalld(payload.getJSONObject("firewall"), checkItems)) {
            return false;
        }
        if (!checkIptables(payload.getJSONObject("iptables"), checkItems)) {
            return false;
        }

        JSONObject sshConfig = payload.getJSONObject("sshConfig");
        if (!checkSshDenyAll(sshConfig, checkItems)) {
            return false;
        }
        if (!checkSshAllow(sshConfig, checkItems)) {
            return false;
        }
        if (!checkSshRootLogin(sshConfig, checkItems)) {
            return false;
        }
        if (!checkSshRootPort(sshConfig, checkItems)) {
            return false;
        }
        return true;
    }

    public boolean checkIptables(JSONObject iptables, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 iptables 服务
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_IPTABLES)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_IPTABLES);
            if (iptables.getBooleanValue("active")) {
                JSONArray ports = iptables.getJSONArray("ports");
                int portNum = ports.size();
                resultOper.setConfigInfo("iptables开放的端口数为：" + portNum);

                if (portNum > 16) {
                    resultOper.setRiskLevel(1);
                    resultOper.setRiskDesc("iptables开放的端口数过多，当前系统开放端口数：" + portNum + "。");
                    resultOper.setSolution("检查系统的端口使用情况，并对未知用途或有风险的端口，用iptables命令关闭。");
                } else {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("iptables服务配置正常，当前开放端口数为：" + portNum + "。");
                    resultOper.setSolution("");
                }
            } else {
                resultOper.setConfigInfo("iptables服务未激活");
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("iptables服务未激活。");
                resultOper.setSolution("检查系统的可用防火墙服务，选择其一启动运行，并通过systemctl enable设置为开机启动服务。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    public boolean checkFirewalld(JSONObject firewalld, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 firewalld 服务
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_FIREWALLD)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_FIREWALLD);
            if (firewalld.getBooleanValue("active")) {
                JSONArray ports = firewalld.getJSONArray("ports");
                int portNum = ports.size();
                resultOper.setConfigInfo("firewalld开放的端口数为：" + portNum);

                if (portNum > 16) {
                    resultOper.setRiskLevel(1);
                    resultOper.setRiskDesc("firewalld开放的端口数过多，当前系统开放端口数：" + portNum + "。");
                    resultOper.setSolution("检查系统的端口使用情况，并对未知用途或有风险的端口，用firewall-cmd关闭。");
                } else {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("firewalld服务配置正常，当前开放端口数为：" + portNum + "。");
                    resultOper.setSolution("");
                }
            } else {
                resultOper.setConfigInfo("firewalld服务未激活");
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("firewalld服务未激活。");
                resultOper.setSolution("检查系统的可用防火墙服务，选择其一启动运行，并通过systemctl enable设置为开机启动服务。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    private boolean checkSshDenyAll(JSONObject ssh, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 ssh 服务拒绝黑名单配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_SSH_DENYALL)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_SSH_DENYALL);
            if (ssh.getBooleanValue("denyAll")) {
                resultOper.setConfigInfo("SSH拒绝黑名单：已配置");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("SSH服务已配置拒绝黑名单。");
                resultOper.setSolution("");
            } else {
                resultOper.setConfigInfo("SSH拒绝黑名单：未配置");
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("SSH服务未配置拒绝黑名单，存在非法连接风险。");
                resultOper.setSolution("系统管理员编辑/etc/hosts.deny文件，添加一行sshd:all:deny，重新启动系统或重新启动sshd服务以生效。");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkSshAllow(JSONObject ssh, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 ssh 服务白名单配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_SSH_ALLOW)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_SSH_ALLOW);
            JSONArray allowIPs = ssh.getJSONArray("allowIPs");
            if (allowIPs.size() > 5) {
                resultOper.setConfigInfo("SSH白名单：" + allowIPs.toJSONString());
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("SSH服务配置的白名单过多，不能有效控制合法的访问连接。");
                resultOper.setSolution("系统管理员检查/etc/hosts.allow文件，移除非法或禁用的IP地址，重新启动系统或重新启动sshd服务以生效。");
            } else if (allowIPs.size() < 1) {
                resultOper.setConfigInfo("SSH白名单：未配置");
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("SSH服务未配置白名单，不能有效控制合法的访问连接。");
                resultOper.setSolution("系统管理员编辑/etc/hosts.allow文件，添加允许的远程连接IP地址，重新启动系统或重新启动sshd服务以生效。");
            } else {
                resultOper.setConfigInfo("SSH白名单：" + allowIPs.toJSONString());
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("SSH服务已配置合理的白名单。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkSshRootLogin(JSONObject ssh, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 ssh 服务对root登录的配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_SSH_ROOT_LOGIN)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_SSH_ROOT_LOGIN);
            if (ssh.getBooleanValue("PermitRootLogin")) {
                resultOper.setConfigInfo("SSH允许root登录：是");
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("SSH服务允许root登录，有较大的远程攻击风险。");
                resultOper.setSolution("系统管理员编辑/etc/ssh/sshd_config文件，配置 PermitRootLogin 为 no，重新启动sshd服务以生效。");
            } else {
                resultOper.setConfigInfo("SSH允许root登录：否");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("SSH服务已禁止root登录，可有效防范远程攻击。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkSshRootPort(JSONObject ssh, JSONObject checkItems) {
        resultOper.setConfigType("服务安全配置");

        // 检查 ssh 服务端口
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.SERVICE_SSH_PORT)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.SERVICE_SSH_PORT);
            int port = ssh.getIntValue("port");
            resultOper.setConfigInfo("SSH端口：" + port);
            if (port < 1000) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("SSH服务端口为" + port + "，需设置为大于1000的随机端口。");
                resultOper.setSolution("系统管理员编辑/etc/ssh/sshd_config文件，配置 Port 为大于1000的端口号，重新启动sshd服务以生效。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("SSH服务已配置合理的端口号：" + port + "。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }
}
