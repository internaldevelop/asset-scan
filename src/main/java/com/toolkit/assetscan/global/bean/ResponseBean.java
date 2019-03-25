package com.toolkit.assetscan.global.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ResponseBean {
    private int code;
    private String error;
    private Object payload;
}
