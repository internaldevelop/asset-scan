package com.toolkit.assetscan.bean.dto;

import com.toolkit.assetscan.bean.po.SystemLogPo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SystemLogsDto extends SystemLogPo {
    private String create_user_name;
    private String create_user_account;
}
