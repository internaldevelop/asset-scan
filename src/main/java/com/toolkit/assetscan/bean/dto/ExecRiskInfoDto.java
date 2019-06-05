package com.toolkit.assetscan.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ExecRiskInfoDto {
    private String result_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp start_time;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp end_time;
    private int run_time;
    private String asset_name;
    private int process_flag;
    private int risk_level;
    private String risk_desc;
    private String solutions;
    private String policy_uuid;
    private String policy_name;
    private String policy_group_uuid;
    private String policy_group_name;

}
