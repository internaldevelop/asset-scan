package com.toolkit.assetscan.global.enumeration;

public enum TaskStatusEnum {
    TASK_LOGICAL_DELETE(-99),
    TASK_INACTIVE(0),   // 任务未激活
    TASK_ACTIVE(1),     // 任务激活/有效
    ;

    private int status;

    TaskStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
