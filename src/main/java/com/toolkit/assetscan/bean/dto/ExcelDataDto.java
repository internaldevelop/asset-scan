package com.toolkit.assetscan.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExcelDataDto implements Serializable {
    private static final long serialVersionUID = 6133772627258154184L;
    /**
     * 表头
     */
    private List<String> titles;

    /**
     * 数据
     */
    private List<List<Object>> rows;

    /**
     * 页签名称
     */
    private String name;
}
