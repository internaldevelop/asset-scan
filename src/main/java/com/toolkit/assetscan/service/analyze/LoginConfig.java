package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginConfig {
    @Autowired
    AnalyzeSubject resultOper;
    // auth     required       pam_tally2.so deny=6 even_deny_root unlock_time=1200 root_unlock_time=1200
    // even_deny_root    也限制root用户；
    // deny           设置普通用户和root用户连续错误登陆的最大次数，超过最大次数，则锁定该用户
    // unlock_time        设定普通用户锁定后，多少时间后解锁，单位是秒；
    // root_unlock_time      设定root用户锁定后，多少时间后解锁，单位是秒；

    public boolean checkLogin(JSONObject login, JSONObject checkItems) {
        if (login.size() == 0) {
            login.put("even_deny_root", false);
            login.put("deny", 0);
            login.put("unlock_time", 0);
            login.put("root_unlock_time", 0);
        }

        if (!checkDenyRoot(login, checkItems)) {
            return false;
        }
        if (!checkDenyCount(login, checkItems)) {
            return false;
        }
        if (!checkUnlockTime(login, checkItems)) {
            return false;
        }
        if (!checkRootUnlockTime(login, checkItems)) {
            return false;
        }

        return true;
    }

    private boolean checkDenyRoot(JSONObject login, JSONObject checkItems) {
        resultOper.setConfigType("login");

        // 检查root用户配置登陆失败锁定
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOGIN_DENY_ROOT)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOGIN_DENY_ROOT);
            if (login.getBooleanValue("even_deny_root")) {
                resultOper.setConfigInfo("root用户登陆失败锁定：已配置");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统已为root用户配置登陆失败锁定。");
                resultOper.setSolution("");
            } else {
                resultOper.setConfigInfo("root用户登陆失败锁定：未配置");
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("系统没有为root用户配置登陆失败锁定，提高了root用户密码被破解的风险。");
                resultOper.setSolution("系统管理员编辑/etc/pam.d/login文件，增加或修改配置行" +
                        "'auth required pam_tally2.so deny=5 even_deny_root'，重启系统以生效。");
            }
            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkDenyCount(JSONObject login, JSONObject checkItems) {
        resultOper.setConfigType("login");

        // 检查登陆失败锁定的最大尝试次数
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOGIN_DENY_COUNT)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOGIN_DENY_COUNT);
            int denyCount = login.getIntValue("deny");
            resultOper.setConfigInfo("登陆失败锁定的最大尝试次数：" + denyCount);
            if (denyCount == 0) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("登陆失败锁定的最大尝试次数设置为：" + denyCount + "，存在用户密码被尝试破解的较大风险。");
                resultOper.setSolution("系统管理员编辑/etc/pam.d/login文件，增加或修改配置行" +
                        "'auth required pam_tally2.so deny=5 even_deny_root'，deny值建议3-5之间，重启系统以生效。");
            } else if (denyCount > 5) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("登陆失败锁定的最大尝试次数设置为：" + denyCount + "，有用户密码被尝试破解的风险。");
                resultOper.setSolution("系统管理员编辑/etc/pam.d/login文件，增加或修改配置行" +
                        "'auth required pam_tally2.so deny=5 even_deny_root'，deny值建议3-5之间，重启系统以生效。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("登陆失败锁定的最大尝试次数设置为：" + denyCount + "，配置符合要求。");
                resultOper.setSolution("");
            }
            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkUnlockTime(JSONObject login, JSONObject checkItems) {
        resultOper.setConfigType("login");

        // 检查用户登陆失败锁定时间
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOGIN_UNLOCK_TIME)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOGIN_UNLOCK_TIME);
            int unlockTime = login.getIntValue("unlock_time");
            resultOper.setConfigInfo("登录失败锁定时间：" + unlockTime + "秒");
            if (unlockTime < 300) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("登录失败锁定时间：" + unlockTime + "秒，锁定时间短会增加密码破解的风险。");
                resultOper.setSolution("系统管理员编辑/etc/pam.d/login文件，增加或修改配置行" +
                        "'auth required pam_tally2.so deny=5 even_deny_root unlock_time=1200 '，" +
                        "unlock_time建议设置在300以上，重启系统以生效。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("登录失败锁定时间：" + unlockTime + "秒，配置符合要求。");
                resultOper.setSolution("");
            }
            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkRootUnlockTime(JSONObject login, JSONObject checkItems) {
        resultOper.setConfigType("login");

        // 检查root用户登陆失败锁定时间
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOGIN_ROOT_UNLOCK_TIME)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOGIN_ROOT_UNLOCK_TIME);
            int unlockTime = login.getIntValue("root_unlock_time");
            resultOper.setConfigInfo("root登录失败锁定时间：" + unlockTime + "秒");
            if (unlockTime < 300) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("root用户登录失败锁定时间：" + unlockTime + "秒，锁定时间短会增加root密码破解的风险。");
                resultOper.setSolution("系统管理员编辑/etc/pam.d/login文件，增加或修改配置行" +
                        "'auth required pam_tally2.so deny=5 even_deny_root root_unlock_time=1200 '，" +
                        "root_unlock_time建议设置在300以上，重启系统以生效。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("root用户登录失败锁定时间：" + unlockTime + "秒，配置符合要求。");
                resultOper.setSolution("");
            }
            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

}
