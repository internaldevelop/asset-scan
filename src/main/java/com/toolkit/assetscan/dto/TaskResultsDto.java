package com.toolkit.assetscan.dto;

import com.toolkit.assetscan.bean.TaskExecuteResultsProps;
import lombok.Data;

/**
 * 任务结果集
 */
@Data
public class TaskResultsDto extends TaskExecuteResultsProps {

    private String task_id;  // 任务id
    private String task_name;  // 任务名称
    private String assets_name;  // 检查目标
    private String assets_ip;  // 目标IP
    private String solutions;  // 解决方案

}
