package com.toolkit.assetscan.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class PasswordProps {
    private String user_uuid;
    private String password;
    private int pwd_mat;
    private int pwd_rat;
}
