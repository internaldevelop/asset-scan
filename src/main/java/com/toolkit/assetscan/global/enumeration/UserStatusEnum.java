package com.toolkit.assetscan.global.enumeration;

public enum UserStatusEnum {
    USER_LOGICAL_DELETE(-99),
    USER_INACTIVE(0),
    USER_ACTIVE(1),
    ;

    private int status;

    UserStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
