package com.toolkit.assetscan.global.enumeration;

public enum DeleteModeEnum {
    PERMANENT(1),
    LOGICAL(2),
    ;

    private int mode;

    DeleteModeEnum(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
