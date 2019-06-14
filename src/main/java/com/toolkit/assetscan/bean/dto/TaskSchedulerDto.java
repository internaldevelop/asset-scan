package com.toolkit.assetscan.bean.dto;

import lombok.Data;

@Data
public class TaskSchedulerDto {
    private String task_uuid;
    private String project_uuid;
    private String user_uuid;
    private String timer_config;
}
