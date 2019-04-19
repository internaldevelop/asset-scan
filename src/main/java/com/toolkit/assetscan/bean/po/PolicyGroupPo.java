package com.toolkit.assetscan.bean.po;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * PolicyGroupProps 对应 groups 表
 * 各字段名称一致，各字段定义略，可参考 groups 表的各字段注释
 */
@Component
@Data
public class PolicyGroupPo {
    private int id;
    private String uuid;
    private String name;
}
