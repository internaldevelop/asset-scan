package com.toolkit.assetscan.global.enumeration;

public enum ReportEnum {

    PATCH_NOT_INSTALLED("1"),  // 补丁安装情况
    SYSTEM_SERVICE("2"),  // 系统服务分析
    SYSTEM_FILE_SERVICE("3"),  // 系统文件安全防护分析
    USER_ACCOUNT_ANALYSIS("4"),  // 用户账号配置分析
    PWD_POLICY_ANALYSIS("5"),  // 口令策略配置分析
    NETWORK_ANALYSIS("6"),  // 网络通信配置分析
    LOG_ANALYSIS("7");  // 日志审计分析

    private String mode;

    ReportEnum(String mode) {
        this.mode = mode;
    }

    public String value() {
        return mode;
    }


}
