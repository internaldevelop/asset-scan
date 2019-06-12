package com.toolkit.assetscan.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * PolicyGroupProps 对应 policy_groups 表
 * 各字段名称一致，各字段定义略，可参考 policy_groups 表的各字段注释
 */
@Component
@Data
public class PolicyGroupPo {
    private int id;
    private String uuid;
    private String name;
    private String code;
    private int type;
    private String create_user_uuid;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
    private int status;
    private int baseline;
}
