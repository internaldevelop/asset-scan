package com.toolkit.assetscan.service.analyze;

public enum BaseLineItemEnum {
    SELINUX_STATUS("SELinux_status", "startup", "检查SELinux是否开启"),
    SELINUX_MODE("SELinux_mode", "startup", "检查SELinux模式是否为enforcing"),
    SELINUX_POLICY("SELinux_policy", "startup", "检查SELinux策略是否为strict"),
    AUTORUN_FIREWALL("AutoRun_firewall", "startup", "防火墙服务开机启动"),
    AUTORUN_EXCLUDED("AutoRun_excluded", "startup", "检查自启动的服务风险"),
    ACCOUNTS_USELESS("Accounts_useless", "accounts", "系统的无用账户核查"),
    ACCOUNTS_REDUNDANT("Accounts_redundant", "accounts", "检查系统的冗余账户"),
    ACCOUNTS_PLAIN_PWD("Accounts_plainpwd", "accounts", "检查账户是否明文密码"),
    ACCOUNTS_ROOT_NUM("Accounts_rootnum", "accounts", "检查系统的root账户数量"),
    GROUPS_PLAIN_PWD("Groups_plainpwd", "accounts", "检查账户组是否明文密码"),
    PWD_WARN_AGE("Password_warnage", "passowrd", "检查密码到期提醒时间"),
    PWD_RETRY("Password_retry", "passowrd", "检查密码最大重试次数"),
    PWD_MINLEN("Password_minlen", "passowrd", "检查密码最小长度"),
    PWD_AGE("Password_age", "passowrd", "检查密码寿命"),
    PWD_DIFOK("Password_difok", "passowrd", "检查密码最少不同字符"),
    PWD_U_CREDIT("Password_ucredit", "passowrd", "检查密码最少大写字母"),
    PWD_L_CREDIT("Password_lcredit", "passowrd", "检查密码最少小写字母"),
    PWD_D_CREDIT("Password_dcredit", "passowrd", "检查密码最少数字"),
    SERVICE_FIREWALLD("Service_firewalld", "services", "firewalld服务配置核查"),
    SERVICE_IPTABLES("Service_iptables", "services", "iptables服务配置核查"),
    SERVICE_SSH_DENYALL("Service_sshdenyall", "services", "ssh服务拒绝黑名单配置核查"),
    SERVICE_SSH_ALLOW("Service_sshallow", "services", "ssh服务白名单配置核查"),
    SERVICE_SSH_ROOT_LOGIN("Service_sshrootlogin", "services", "检查ssh服务是否允许root登录"),
    SERVICE_SSH_PORT("Service_sshport", "services", "检查ssh服务端口"),
    LOGIN_DENY_ROOT("Login_denyroot", "login", "root用户配置登陆失败锁定"),
    LOGIN_DENY_COUNT("Login_denycount", "login", "登陆失败锁定的最大尝试次数"),
    LOGIN_UNLOCK_TIME("Login_unlocktime", "login", "检查普通用户的登陆锁定时间"),
    LOGIN_ROOT_UNLOCK_TIME("Login_rootunlocktime", "login", "检查root用户的登陆锁定时间"),
    LOG_MESSAGES("Log_messages", "syslog", "核查消息类日志配置"),
    LOG_SECURE("Log_secure", "syslog", "核查系统安全类日志配置"),
    LOG_MAILLOG("Log_maillog", "syslog", "核查邮件类日志配置"),
    LOG_CRON("Log_cron", "syslog", "核查定时任务日志配置"),
    LOG_EMERGENCY("Log_emergency", "syslog", "核查系统严重级别信息处理配置"),
    LOG_BOOT("Log_boot", "syslog", "核查系统启动日志配置"),
    LOG_ROTATE("Log_rotate", "syslog", "核查日志轮转配置"),
    IPT_ACTIVE("IPT_active", "iptables", "检查iptables服务是否激活"),
    IPT_INPUT_BLACKLIST("IPT_inputblack", "iptables", "核查iptables网络接入黑名单配置"),
    IPT_INPUT_WHITELIST("IPT_inputwhite", "iptables", "核查iptables网络接入白名单配置"),
    IPT_OUTPUT_BLACKLIST("IPT_outputblack", "iptables", "核查iptables网络外接黑名单配置"),
    IPT_OUTPUT_WHITELIST("IPT_outputwhite", "iptables", "核查iptables网络外接白名单配置"),
    IPT_FWD_BLACKLIST("IPT_fwdblack", "iptables", "核查iptables网络转发黑名单配置"),
    IPT_FWD_WHITELIST("IPT_fwdwhite", "iptables", "核查iptables网络转发白名单配置"),
    ;
    private String name;
    private String group;
    private String desc;

    BaseLineItemEnum(String name, String group, String desc) {
        this.name = name;
        this.group = group;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
