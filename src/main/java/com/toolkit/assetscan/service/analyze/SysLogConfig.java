package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class SysLogConfig {
    @Autowired
    AnalyzeSubject resultOper;

    public boolean checkSysLog(JSONObject sysLog, JSONObject checkItems) {
        JSONArray rules = sysLog.getJSONArray("rules");
        JSONObject rotate = sysLog.getJSONObject("rotate");

        if (!checkMessagesConfig(rules, checkItems)) {
            return false;
        }
        if (!checkSecureConfig(rules, checkItems)) {
            return false;
        }
        if (!checkMailConfig(rules, checkItems)) {
            return false;
        }
        if (!checkCronConfig(rules, checkItems)) {
            return false;
        }
        if (!checkEmergencyConfig(rules, checkItems)) {
            return false;
        }
        if (!checkBootConfig(rules, checkItems)) {
            return false;
        }
        if (!checkRotate(rotate, checkItems)) {
            return false;
        }

        return true;
    }

    private boolean checkMessagesConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查消息日志的配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_MESSAGES)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_MESSAGES);

            List<String> riskDesc = new ArrayList<>();
            boolean wrongConfig = false;
            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String action = rule.getString("action");
                // 在规则中查找messages的配置
                if (!action.endsWith("messages"))
                    continue;

                String content = rule.getString("content");
                resultOper.setConfigInfo("消息类日志配置：" + content);
                // 邮件日志配置
                if (!content.contains("mail.none")) {
                    wrongConfig = true;
                    riskDesc.add("系统未禁止邮件日志写入到普通消息日志文件中");
                } else {
                    riskDesc.add("系统已关闭所有等级的邮件日志写入到普通消息日志文件中");
                }
                // 身份校验日志配置
                if (!content.contains("authpriv.none")) {
                    wrongConfig = true;
                    riskDesc.add("系统未禁止身份校验日志写入到普通消息日志文件中");
                } else {
                    riskDesc.add("系统已关闭所有等级的身份校验日志写入到普通消息日志文件中");
                }
                // 定时任务日志配置
                if (!content.contains("cron.none")) {
                    wrongConfig = true;
                    riskDesc.add("系统未禁止定时任务日志写入到普通消息日志文件中");
                } else {
                    riskDesc.add("系统已关闭所有等级的定时任务日志写入到普通消息日志文件中");
                }

                if (wrongConfig) {
                    resultOper.setRiskLevel(3);
                    resultOper.setRiskDesc(String.join("；", riskDesc));
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置消息日志：" +
                            "*.info;mail.none;authpriv.none;cron.none    /var/log/messages 。");
                } else {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc(String.join("；", riskDesc));
                    resultOper.setSolution("");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;

                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkSecureConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查身份认证安全消息配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_SECURE)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_SECURE);

            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String action = rule.getString("action");

                // 在规则中查找secure的配置
                if (!action.endsWith("secure"))
                    continue;

                String content = rule.getString("content");
                resultOper.setConfigInfo("安全类日志配置：" + content);

                if (content.equals("authpriv.*")) {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("已正确配置将所有级别的系统安全类日志写入到secure文件中。");
                    resultOper.setSolution("");
                } else {
                    resultOper.setRiskLevel(2);
                    resultOper.setRiskDesc("未正确配置secure日志文件的写入内容，当前配置为：" + content + "。");
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置安全类日志：" +
                            "authpriv.*   /var/log/secure 。");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;
                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkMailConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查邮件类消息配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_MAILLOG)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_MAILLOG);

            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String action = rule.getString("action");

                // 在规则中查找maillog的配置
                if (!action.endsWith("maillog"))
                    continue;

                String content = rule.getString("content");
                resultOper.setConfigInfo("邮件类日志配置：" + content);

                if (content.equals("mail.*")) {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("已正确配置将所有级别的邮件类日志写入到maillog文件中。");
                    resultOper.setSolution("");
                } else {
                    resultOper.setRiskLevel(1);
                    resultOper.setRiskDesc("未正确配置maillog日志文件的写入内容，当前配置为：" + content + "。");
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置邮件类日志：" +
                            "mail.*   /var/log/maillog 。");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;
                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkCronConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查定时任务消息配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_CRON)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_CRON);

            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String action = rule.getString("action");

                // 在规则中查找cron的配置
                if (!action.endsWith("cron"))
                    continue;

                String content = rule.getString("content");
                resultOper.setConfigInfo("定时任务日志配置：" + content);

                if (content.equals("cron.*")) {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("已正确配置将所有级别的定时任务日志写入到cron文件中。");
                    resultOper.setSolution("");
                } else {
                    resultOper.setRiskLevel(1);
                    resultOper.setRiskDesc("未正确配置cron日志文件的写入内容，当前配置为：" + content + "。");
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置定时任务日志：" +
                            "cron.*   /var/log/cron 。");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;
                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkEmergencyConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查系统紧急消息配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_EMERGENCY)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_EMERGENCY);

            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String content = rule.getString("content");
                // 在规则中查找紧急消息的配置
                if (!content.equals("*.emerg"))
                    continue;

                String action = rule.getString("action");
                resultOper.setConfigInfo("系统紧急消息的日志配置：" + action);

                if (action.equals(":omusrmsg:*") || action.equals("*")) {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("已正确配置将所有系统紧急消息日志发给所有账户。");
                    resultOper.setSolution("");
                } else {
                    resultOper.setRiskLevel(2);
                    resultOper.setRiskDesc("未配置将系统紧急消息日志发给所有账户。");
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置系统紧急消息日志：" +
                            "*.emerg    :omusrmsg:* 。");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;
                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkBootConfig(JSONArray rules, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查系统启动消息配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_BOOT)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_BOOT);

            for (Iterator iter = rules.iterator(); iter.hasNext(); ) {
                JSONObject rule = (JSONObject) iter.next();
                String action = rule.getString("action");

                // 在规则中查找boot的配置
                if (!action.contains("boot"))
                    continue;

                String content = rule.getString("content");
                resultOper.setConfigInfo("系统启动日志配置：" + content);

                if (content.equals("local7.*")) {
                    resultOper.setRiskLevel(0);
                    resultOper.setRiskDesc("已正确配置将所有级别的系统启动日志写入到boot文件中。");
                    resultOper.setSolution("");
                } else {
                    resultOper.setRiskLevel(2);
                    resultOper.setRiskDesc("未正确配置boot日志文件的写入内容，当前配置为：" + content + "。");
                    resultOper.setSolution("系统管理员修改/etc/rsyslog.conf文件，按如下方式正确配置系统启动日志：" +
                            "local7.*   /var/log/boot.log 。");
                }

                // 保存核查记录
                if (!resultOper.saveCheckResult())
                    return false;
                // 处理完配置后，不再检查其他项
                break;
            }
        }

        return true;
    }

    private boolean checkRotate(JSONObject rotate, JSONObject checkItems) {
        resultOper.setConfigType("日志安全配置");

        // 检查消息轮转配置
        if (resultOper.needCheck(checkItems, BaseLineItemEnum.LOG_ROTATE)) {
            resultOper.saveCheckItem(checkItems, BaseLineItemEnum.LOG_ROTATE);

            // 轮转周期和最多保留日志文档数量
            int maxDays = rotate.getIntValue("maxDays");
            int reservedFilesCount = rotate.getIntValue("reservedFilesCount");
//            String frequency = rotate.getString("frequency");
            String configInfo = String.format("日志轮转配置：轮转周期%1$s天，最多保留%2$s个日志文档。", maxDays, reservedFilesCount);
            resultOper.setConfigInfo(configInfo);

            if (reservedFilesCount < 4) {
                resultOper.setRiskLevel(1);
                resultOper.setRiskDesc("系统的日志轮转配置存在缺陷，" + configInfo);
                resultOper.setSolution("系统管理员修改/etc/logrotate.conf文件，按如下方式正确配置日志轮转：" +
                        "（1）monthly；（2）rotate 5 。");
            } else {
                resultOper.setRiskLevel(0);
                resultOper.setRiskDesc("系统的日志轮转配置正确，" + configInfo);
                resultOper.setSolution("");
            }
            // 保存核查记录
            if (!resultOper.saveCheckResult())
                return false;
        }
        return true;
    }
}
