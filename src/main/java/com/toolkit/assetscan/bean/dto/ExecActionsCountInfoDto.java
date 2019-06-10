package com.toolkit.assetscan.bean.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ExecActionsCountInfoDto extends ExecActionsInfoDto {
    private int exec_count;
}
