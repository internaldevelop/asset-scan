package com.toolkit.assetscan.global.enumeration;

public enum PolicyStatusEnum {
    LOGICAL_DELETE(-99),
    INVALID(0),
    VALID(1),
    ;

    private int status;

    PolicyStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
