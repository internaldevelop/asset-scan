package com.toolkit.assetscan.bean.po;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BaseLinePo {
    private int id;
    private int level;
    private String templates;
}
