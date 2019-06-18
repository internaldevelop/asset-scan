package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class SystemLogPo {
    private int id;
    private String uuid;
//    日志记录的类型：
//            1：成功操作；
//            2：失败操作；
//            3：系统错误（严重错误）；
//            4：一般信息；
//            5：异常信息（一般异常）；
    private int type;
    private String title;
    private String contents;
    private String create_user_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
