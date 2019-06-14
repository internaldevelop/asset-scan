package com.toolkit.assetscan.global.enumeration;

public enum ScheduleModeEnum {
    NONE(0),            // 非计划
    EVERY_DAY(1),       // 每天计划执行
    ;

    private int mode;

    ScheduleModeEnum(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
