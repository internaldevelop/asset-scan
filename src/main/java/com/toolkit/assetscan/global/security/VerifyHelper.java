package com.toolkit.assetscan.global.security;

import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class VerifyHelper {
    public ErrorCodeEnum verifyUserPassword(String pwdNeedCheck, String userPwd) {
        if (userPwd.compareTo(pwdNeedCheck) == 0)
            return ErrorCodeEnum.ERROR_OK;
        else
            return ErrorCodeEnum.ERROR_INVALID_PASSWORD;
    }
}
