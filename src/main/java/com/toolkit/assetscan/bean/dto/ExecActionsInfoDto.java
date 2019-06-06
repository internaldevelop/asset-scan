package com.toolkit.assetscan.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ExecActionsInfoDto {
    private String action_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp exec_time;
    private String task_uuid;
    private String task_name;
    private String project_uuid;
    private String project_name;
    private String operator_uuid;
    private String operator_account;
    private String operator_name;
    private String asset_uuid;
    private String asset_name;
}
