package com.toolkit.assetscan.global.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class SockMsgBean {
    private int type;
    private String message;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp timeStamp;
    private Object payload;
}
