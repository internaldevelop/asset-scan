package com.toolkit.assetscan.global.enumeration;

public enum ReportEnum {

    /*PATCH_NOT_INSTALLED("1"),  // 补丁安装情况
    SYSTEM_SERVICE("2"),  // 系统服务分析
    SYSTEM_FILE_SERVICE("3"),  // 系统文件安全防护分析
    USER_ACCOUNT_ANALYSIS("4"),  // 用户账号配置分析
    PWD_POLICY_ANALYSIS("5"),  // 口令策略配置分析
    NETWORK_ANALYSIS("6"),  // 网络通信配置分析
    LOG_ANALYSIS("7");  // 日志审计分析*/

    GROUP_WINDOWS_PATCH_INSTALL("WinPatchInstall"), //Windows系统补丁安装策略
    GROUP_WINDOWS_SERVICES("WinServices"), //Windows系统服务情况策略
    GROUP_WINDOWS_FILE_SECURITY("WinSysFileProtect"), //Windows系统文件安全防护策略
    GROUP_LINUX_PATCH_INSTALL("LinuxPatchInstall"), //Linux系统补丁安装策略
    GROUP_LINUX_SERVICES("LinuxServices"), //Linux系统服务情况策略
    GROUP_LINUX_FILE_SECURITY("LinuxSysFileProtect"), //Linux系统文件安全防护策略
    GROUP_WINDOWS_USER_ACCOUNT_CONFIGURATION("WinUserAccountConfig"), //Windows用户账号配置策略
    GROUP_LINUX_USER_ACCOUNT_CONFIGURATION("LinuxUserAccountConfig"), //Linux用户账号配置策略
    GROUP_WINDOWS_PASSWORD_CONFIGURATION("WinUserPwdConfig"), //Windows口令配置策略
    GROUP_LINUX_PASSWORD_CONFIGURATION("LinuxUserPwdConfig"), //Linux口令配置策略
    GROUP_WINDOWS_NETWORK_COMMUNICATION_CONFIGURATION("WinNetworkCommConfig"), //Windows网络通信配置策略
    GROUP_LINUX_NETWORK_COMMUNICATION_CONFIGURATION("LinuxNetworkCommConfig"), //Linux网络通信配置策略
    GROUP_WINDOWS_LOG_AUDIT_CONFIGURATION("WinLogAuditConfig"), //Windows日志审计配置策略
    GROUP_LINUX_LOG_AUDIT_CONFIGURATION("LinuxLogAuditConfig"), //Linux日志审计配置策略
    //GROUP_SECURITY_AUDIT_CONFIGURATION(11), //安全审计策略
    GROUP_WINDOWS_SYSTEM_FIREWALL_SECURITY("WinFirewallConfig"), //Windows系统防火墙安全策略
    GROUP_LINUX_SYSTEM_FIREWALL_SECURITY("LinuxFirewallConfig"); //Linux系统防火墙安全策略

    private String mode;

    ReportEnum(String mode) {
        this.mode = mode;
    }

    public String value() {
        return mode;
    }


}
