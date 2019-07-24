package com.toolkit.assetscan.bean.dto;

import lombok.Data;

@Data
public class CheckStatisticsDto {
    private String config_type;
    private String risk_level;
    private int count;
}
