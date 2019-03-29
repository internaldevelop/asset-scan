package com.toolkit.assetscan.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * UserProps 对应数据库中users表，
 * 各字段名称一致，各字段定义略，可参考users表的各字段注释
 */
@Component
@Data
public class UserProps {
    private int id;
    private String uuid;
    private String account;
    private String password;
    private String password_salt;
    private int pwd_mat;
    private int pwd_rat;
    // 不能用group，否则含group字段的SQL语句执行失败
    private int user_group;
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
