package com.toolkit.assetscan.bean.po;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SystemConfigPo {
    private int id;
    private String name;
    private String value;
}
