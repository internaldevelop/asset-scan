package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Component
public class AccountConfig {
    @Autowired
    AnalyzeSubject resultOper;

    public boolean checkAccounts(JSONArray accounts, JSONObject checkItems) {
        if (!checkUselessAccounts(accounts, checkItems)) {
            return false;
        }
        if (!checkRedudantAccounts(accounts, checkItems)) {
            return false;
        }
        if (!checkPlainTextPwd(accounts, checkItems)) {
            return false;
        }
        if (!checkRootCount(accounts, checkItems)) {
            return false;
        }

        return true;
    }

    private boolean checkUselessAccounts(JSONArray accounts, JSONObject checkItems) {
        resultOper.setConfigType("账户安全配置");

        // 检查系统的无用账户
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.ACCOUNTS_USELESS)) {
            // 无用账户列表
            String checkUsers = "hpsmh,named,uucp,nuucp,adm,daemon,bin,lp,sync,shutdown,halt,mail,sys,nobody";
            boolean hasUselessAccount = false;
            List<String> accList = new ArrayList<>();
            List<String> uselessList = Arrays.asList(checkUsers.split(","));
            for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
                JSONObject account = (JSONObject) iter.next();
                String accountName = account.getString("account");
                // 系统账户列表中含有无用账户，并且未锁定
                if (uselessList.contains(accountName) && !account.getString("shell").endsWith("nologin")) {
                    hasUselessAccount = true;
                    accList.add(accountName);
                }
            }

            String configInfo = "无用账户：";
            String uselessAccs = String.join(" | ", accList);

            // 判断无用账户的情况，并设置核查结果
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.ACCOUNTS_USELESS);
            if (hasUselessAccount) {
                resultOper.setConfigInfo(configInfo + uselessAccs);
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统中有无用账户（" + uselessAccs + "）需要管理员进行清理。");
                resultOper.setSolution("管理员查看/etc/passwd文件，确认问题描述的账户是无用账户，并对这些账户进行锁定或删除。");
            } else {
                resultOper.setConfigInfo(configInfo + "无");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统中未发现无用账户。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    private boolean checkRedudantAccounts(JSONArray accounts, JSONObject checkItems) {
        resultOper.setConfigType("账户安全配置");

        // 检查系统的冗余账户
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.ACCOUNTS_REDUNDANT)) {
            List<String> accList = new ArrayList<>();
            for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
                JSONObject account = (JSONObject) iter.next();
                String accountName = account.getString("account");
                // 记录未锁定的系统账户
                if (!account.getString("shell").endsWith("nologin")) {
                    accList.add(accountName);
                }
            }

            // 设置采集的账户信息
            String configInfo = "可用账户：";
            String foundAccs = String.join(" | ", accList);
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.ACCOUNTS_REDUNDANT);
            resultOper.setConfigInfo(configInfo + foundAccs);

            // 判断冗余账户的情况，并设置核查结果
            if (accList.size() > 5) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统中可用账户较多：" + foundAccs + "。需检查各账户的必要性。");
                resultOper.setSolution("管理员查看/etc/passwd文件，确认账户是否合法账户，并对冗余账户进行锁定或删除。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("未发现冗余账户。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }

        return true;
    }

    private boolean checkPlainTextPwd(JSONArray accounts, JSONObject checkItems) {
        resultOper.setConfigType("账户安全配置");

        // 检查账户是否明文密码
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.ACCOUNTS_PLAIN_PWD)) {
            List<String> accList = new ArrayList<>();
            for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
                JSONObject account = (JSONObject) iter.next();
                String accountName = account.getString("account");
                // 记录明文密码的账户
                if (!account.getString("password").equalsIgnoreCase("x")) {
                    accList.add(accountName);
                }
            }

            // 设置采集的账户信息
            String configInfo = "明文密码账户：";
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.ACCOUNTS_PLAIN_PWD);
            if (accList.size() > 0) {
                String foundAccs = String.join(" | ", accList);
                resultOper.setConfigInfo(configInfo + foundAccs);
                resultOper.setRiskLevel(3);
                resultOper.setRiskDesc("系统存在明文密码的账户：" + foundAccs + "。需重新设置这些账户的密码。");
                resultOper.setSolution("管理员对明文密码的用户执行passwd命令，修改密码。");
            } else {
                resultOper.setConfigInfo(configInfo + "未发现");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统中未发现明文密码的账户。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkRootCount(JSONArray accounts, JSONObject checkItems) {
        resultOper.setConfigType("账户安全配置");

        // 检查系统中root账户数量
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.ACCOUNTS_ROOT_NUM)) {
            List<String> accList = new ArrayList<>();
            for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
                JSONObject account = (JSONObject) iter.next();
                String accountName = account.getString("account");
                // 记录root账户
                if (account.getString("UID").equals("0")) {
                    accList.add(accountName);
                }
            }

            // 设置采集的账户信息
            String configInfo = "root账户：";
            String foundAccs = String.join(" | ", accList);
            resultOper.setConfigInfo(configInfo + foundAccs);
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.ACCOUNTS_ROOT_NUM);
            if (accList.size() > 1) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统中有多个root账户：" + foundAccs + "。");
                resultOper.setSolution("管理员以root账户登录，检查各账户的权限等级，并调整不合理的账户权限。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统的root账户正常。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    public boolean checkGroupPwd(JSONArray groups, JSONObject checkItems) {
        resultOper.setConfigType("账户安全配置");

        // 检查账户组是否明文密码
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.GROUPS_PLAIN_PWD)) {
            List<String> groupList = new ArrayList<>();
            for (Iterator iter = groups.iterator(); iter.hasNext(); ) {
                JSONObject group = (JSONObject) iter.next();
                String groupName = group.getString("group");
                // 记录明文密码的账户组
                if (!group.getString("group_pwd").equalsIgnoreCase("x")) {
                    groupList.add(groupName);
                }
            }

            // 设置采集的账户组信息
            String configInfo = "明文密码账户组：";
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.GROUPS_PLAIN_PWD);
            if (groupList.size() > 0) {
                String foundGroups = String.join(" | ", groupList);
                resultOper.setConfigInfo(configInfo + foundGroups);
                resultOper.setRiskLevel(3);
                resultOper.setRiskDesc("系统存在明文密码的账户组：" + foundGroups + "。需配置账户组密码为密文。");
                resultOper.setSolution("管理员打开/etc/passwd文件，找到明文密码的账户组，修改密码域为‘x’（密文）。");
            } else {
                resultOper.setConfigInfo(configInfo + "未发现");
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统中未发现明文密码的账户组。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }
}
