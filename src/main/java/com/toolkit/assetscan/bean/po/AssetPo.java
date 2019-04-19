package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class AssetPo {
    private int id;
    private String uuid;
    private String name;
    private String code;
    private String ip;
    private String port;
    private String user;
    private String password;
    private String os_type;
    private String os_ver;
    private String create_user_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
