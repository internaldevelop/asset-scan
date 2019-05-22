package com.toolkit.assetscan.global.enumeration;

public enum ProjectRunTimeModeEnum {
    MODE_NOW(1),//运行时间模式：1：立即运行；2:30分钟后运行；3:1小时后运行；4:24小时后运行
    MODE_30MINS_LATER(2),
    MODE_1HOUR_LATER(3),
    MODE_1DAY_LATER(4),
    ;

    private int runTimeMode;

    ProjectRunTimeModeEnum(int runTimeMode) {
        this.runTimeMode = runTimeMode;
    }

    public int getRunTimeMode() {
        return runTimeMode;
    }

    public void setRunTimeMode(int runTimeMode) {
        this.runTimeMode = runTimeMode;
    }
}
