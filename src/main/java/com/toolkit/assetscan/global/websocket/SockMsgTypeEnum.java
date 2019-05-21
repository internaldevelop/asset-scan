package com.toolkit.assetscan.global.websocket;

public enum SockMsgTypeEnum {
    GENERAL_INFO(0, "一般信息"),        // payload为字符串
    SINGLE_TASK_RUN_INFO(1, "单个任务运行状态"),    // payload为单个任务的状态数据对象
    MULTIPLE_TASK_RUN_INFO(2, "多个任务运行状态"),  // payload为多个任务的状态数据对象的集合（JSON数组）
    ;

    private int type;
    private String message;

    SockMsgTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
