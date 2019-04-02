package com.toolkit.assetscan.global.params;

import com.toolkit.assetscan.global.utils.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CheckParams {
    public boolean isValidUserUuid(String userUuid) {
        if (!StringUtils.isValid(userUuid))
            return false;

        return true;
    }

    public boolean isValidPassword(String password) {
        if (!StringUtils.isValid(password))
            return false;

        return true;
    }
}
