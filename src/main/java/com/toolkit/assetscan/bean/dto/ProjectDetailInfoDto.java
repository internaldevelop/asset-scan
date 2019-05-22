package com.toolkit.assetscan.bean.dto;

import com.toolkit.assetscan.bean.po.ProjectPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectDetailInfoDto extends ProjectPo {
    private String task_name;  // 任务名称
    private String asset_uuid;
    private String policy_groups; // 策略组的集合
}
