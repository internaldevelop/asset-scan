package com.toolkit.assetscan.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toolkit.assetscan.bean.po.TaskPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务信息表，其中包括部分资产信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskInfosDto extends TaskPo {
    private String uuid;
    private String task_id;  // 任务id
    private String task_name;  // 任务名称
    private int status;        // 任务运行状态
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp update_time; //任务更新时间
    private String assets_uuid;  // 主机uuid
    private String assets_name;  // 主机名称
    private String assets_ip;  // 主机ip
    private String assets_port;  // 主机port
    private String os_type;  // 系统类型
    private String os_ver;  // 系统版本
}
