package com.toolkit.assetscan.bean.dto;

import com.toolkit.assetscan.bean.po.TaskExecuteResultsPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务结果集
 */
@Data
public class TaskResultsStatisticsDto {

    private String os_type;  // 系统名称

    private String policy_name;  // 策略名称

    private int num; // 数量

}
