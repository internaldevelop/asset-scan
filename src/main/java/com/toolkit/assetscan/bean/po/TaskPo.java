package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class TaskPo {
    private int id;
    private String uuid;
    private String name;
    private String code;
    private String description;
    private String asset_uuid;
    private String policies_name;
    private String create_user_uuid;
    private int status;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
