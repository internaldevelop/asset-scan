package com.toolkit.assetscan.bean.dto;

import com.toolkit.assetscan.bean.po.PolicyPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PolicyDetailInfoDto extends PolicyPo {
    private String assets_name;  // 设备名称
    private String assets_ip;  // 设备IP
    private String group_name;  // 策略名称
}
