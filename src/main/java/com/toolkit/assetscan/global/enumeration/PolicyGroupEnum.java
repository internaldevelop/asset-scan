package com.toolkit.assetscan.global.enumeration;

public enum PolicyGroupEnum {
    GROUP_WINDOWS_PATCH_INSTALL(1), //Windows系统补丁安装策略
    GROUP_WINDOWS_SERVICES(2), //Windows系统服务情况策略
    GROUP_WINDOWS_FILE_SECURITY(3), //Windows系统文件安全防护策略
    GROUP_LINUX_PATCH_INSTALL(4), //Linux系统补丁安装策略
    GROUP_LINUX_SERVICES(5), //Linux系统服务情况策略
    GROUP_LINUX_FILE_SECURITY(6), //Linux系统文件安全防护策略
    GROUP_USER_ACCOUNT_CONFIGURATION(7), //用户账号配置策略
    GROUP_PASSWORD_CONFIGURATION(8), //口令配置策略
    GROUP_NETWORK_COMMUNICATION_CONFIGURATION(9), //网络通信配置策略
    GROUP_LOG_AUDIT_CONFIGURATION(10), //日志审计配置策略
    GROUP_SECURITY_AUDIT_CONFIGURATION(11), //安全审计策略
    ;

    private int groupId;

    PolicyGroupEnum(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }
}
