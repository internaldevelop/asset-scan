package com.toolkit.assetscan.global.enumeration;

public enum ReportEnum {

    PATCH_NOT_INSTALLED("1"),  // 补丁安装情况
    SYSTEM_SERVICE("2");  // 系统服务分析


    private String mode;

    ReportEnum(String mode) {
        this.mode = mode;
    }

    public String value() {
        return mode;
    }


}
