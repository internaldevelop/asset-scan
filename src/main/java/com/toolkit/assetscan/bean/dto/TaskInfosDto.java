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
    // 资产信息
    private String assets_uuid;  // 主机uuid
    private String assets_name;  // 主机名称
    private String assets_ip;  // 主机ip
    private String assets_port;  // 主机port
    private String os_type;  // 系统类型
    private String os_ver;  // 系统版本
    // 创建该任务用户信息
    private String user_uuid;   // 创建该任务的用户uuid
    private String user_name;   // 创建该任务的用户姓名
    private String user_account;   // 创建该任务的用户账户
}
