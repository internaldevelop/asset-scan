package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class PasswordConfig {
    @Autowired
    AnalyzeSubject resultOper;

    public boolean checkPassword(JSONObject pwdConfig, JSONObject checkItems) {
        if (!checkPwdWarnAge(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdRetry(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdMinlen(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdAge(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdDifok(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdUcredit(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdLcredit(pwdConfig, checkItems)) {
            return false;
        }
        if (!checkPwdDcredit(pwdConfig, checkItems)) {
            return false;
        }

        return true;
    }

    private boolean checkPwdWarnAge(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码到期提醒时间
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_WARN_AGE)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_WARN_AGE);
            String configInfo = "密码到期提醒时间：";
            int warnAge = pwdConfig.getIntValue("PASS_WARN_AGE");
            resultOper.setConfigInfo(configInfo + warnAge + "天");
            if (warnAge < 7) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统密码到期提醒时间太短：" + warnAge + "天。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/login.defs文件中的PASS_WARN_AGE的值，建议值为7-30。");
            } else if (warnAge > 30) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统密码到期提醒时间太长：" + warnAge + "天。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/login.defs文件中的PASS_WARN_AGE的值，建议值为7-30。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统密码到期提醒时间配置正确，当前配置为：" + warnAge + "天。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdRetry(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码重试次数
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_RETRY)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_RETRY);
            String configInfo = "密码最大重试次数：";
            int retry = pwdConfig.getIntValue("PASS_retry");
            resultOper.setConfigInfo(configInfo + retry);
            if (retry > 10) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("系统登录时最大重试次数过多：" + retry + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password retry的值，建议值为3-10。");
            } else if (retry < 3) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统登录时最大重试次数过少：" + retry + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password retry的值，建议值为3-10。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统登录的最大重试次数配置正确，当前配置为：" + retry + "。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdMinlen(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码最小长度
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_MINLEN)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_MINLEN);
            String configInfo = "密码最小长度：";
            int minLen = pwdConfig.getIntValue("PASS_MIN_LEN");
            resultOper.setConfigInfo(configInfo + minLen);
            if (minLen < 10) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码最小长度过短：" + minLen + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/login.defs文件中的PASS_MIN_LEN的值，建议长度最少为10。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("密码最小长度配置正确，当前配置为：" + minLen + "。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdAge(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码寿命
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_AGE)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_AGE);
            int maxDays = pwdConfig.getIntValue("PASS_MAX_DAYS");
            int minDays = pwdConfig.getIntValue("PASS_MIN_DAYS");
            resultOper.setConfigInfo("密码最长过期天数：" + maxDays + "天；密码最短过期天数：" + minDays + "天。");
            if (maxDays > 365) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码最长过期天数过长：" + maxDays + "天；提高了账号密码被破解的风险。");
                resultOper.setSolution("系统管理员以root账户登录，设置/etc/login.defs文件中的PASS_MAX_DAYS的值，建议值为90到365。");
            } else if (maxDays < 90) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("密码最长过期天数过短：" + maxDays + "天。");
                resultOper.setSolution("系统管理员以root账户登录，设置/etc/login.defs文件中的PASS_MAX_DAYS的值，建议值为90到365。");
            } else if (minDays > 365) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码最小过期天数过长：" + minDays + "天。");
                resultOper.setSolution("系统管理员以root账户登录，设置/etc/login.defs文件中的PASS_MIN_DAYS的值，建议值为90到365。");
            } else if (minDays > maxDays) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("密码最小过期天数（" + minDays + "）天，大于密码最长过期天数（" + maxDays + "）。");
                resultOper.setSolution("系统管理员以root账户登录，设置/etc/login.defs文件中的PASS_MAX_DAYS和PASS_MIN_DAYS的值，" +
                        "建议值为90到365，并且PASS_MAX_DAYS不小于PASS_MIN_DAYS。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("密码寿命区间配置正确，当前配置为：" + minDays + "天--" + maxDays + "天。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdDifok(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码最少不同字符
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_DIFOK)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_DIFOK);
            String configInfo = "密码允许的最少不同字符：";
            int difok = pwdConfig.getIntValue("PASS_difok");
            resultOper.setConfigInfo(configInfo + difok);
            if (difok < 5) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码复杂度要求偏低，当前系统允许的最少不同字符为：" + difok + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password difok的值，建议值大于5。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统要求密码的最少不同字符为：" + difok + "，符合密码要求。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdUcredit(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码最少大写字母
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_U_CREDIT)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_U_CREDIT);
            String configInfo = "密码允许的最少大写字母：";
            int ucredit = pwdConfig.getIntValue("PASS_ucredit");
            resultOper.setConfigInfo(configInfo + ucredit);
            if (ucredit < 1) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码复杂度要求偏低，当前系统允许的最少大写字母为：" + ucredit + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password ucredit的值，建议值不小于1。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统要求密码的最少大写字母为：" + ucredit + "，符合密码要求。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdDcredit(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码最少数字
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_D_CREDIT)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_D_CREDIT);
            String configInfo = "密码允许的最少数字：";
            int dcredit = pwdConfig.getIntValue("PASS_dcredit");
            resultOper.setConfigInfo(configInfo + dcredit);
            if (dcredit < 3) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码复杂度要求偏低，当前系统允许的最少数字为：" + dcredit + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password dcredit的值，建议值不小于3。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统要求密码的最少数字为：" + dcredit + "，符合密码要求。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

    private boolean checkPwdLcredit(JSONObject pwdConfig, JSONObject checkItems) {
        resultOper.setConfigType("密码配置");

        // 检查密码最少小写字母
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.PWD_L_CREDIT)) {
            // 设置采集的密码配置信息
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.PWD_L_CREDIT);
            String configInfo = "密码允许的最少小写字母：";
            int lcredit = pwdConfig.getIntValue("PASS_lcredit");
            resultOper.setConfigInfo(configInfo + lcredit);
            if (lcredit < 3) {
                resultOper.setRiskLevel(2);
                resultOper.setRiskDesc("密码复杂度要求偏低，当前系统允许的最少小写字母为：" + lcredit + "。");
                resultOper.setSolution("系统管理员以root账户登录，修改/etc/pam.d/system-auth文件中的password lcredit的值，建议值不小于3。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统要求密码的最少小写字母为：" + lcredit + "，符合密码要求。");
                resultOper.setSolution("");
            }

            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }

}
