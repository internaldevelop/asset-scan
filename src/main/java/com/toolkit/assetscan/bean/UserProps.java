package com.toolkit.assetscan.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserProps {
    private int id;
    private String uuid;
    private String account;
    private String password;
    private String password_salt;
    private int status;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String description;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp expire_time;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private java.sql.Timestamp create_time;
}
