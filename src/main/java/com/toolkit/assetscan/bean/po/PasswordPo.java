package com.toolkit.assetscan.bean.po;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PasswordPo {
    private String user_uuid;
    private String password;
    private int pwd_mat;
    private int pwd_rat;
    private int user_status;
    private int user_group;
    private String email;
}
