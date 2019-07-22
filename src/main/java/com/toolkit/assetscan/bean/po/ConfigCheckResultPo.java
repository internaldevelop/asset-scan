package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConfigCheckResultPo {
    private int id;
    private String uuid;
    private int base_line;
    private String asset_uuid;
    private String scan_uuid;
    private String config_type;
    private String config_info;
    private String check_item;
    private int risk_level;
    private String risk_desc;
    private String solution;
    private String creator_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
