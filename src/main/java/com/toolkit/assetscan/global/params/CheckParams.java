package com.toolkit.assetscan.global.params;

import org.springframework.stereotype.Component;

@Component
public class CheckParams {
    public boolean isValidUserUuid(String userUuid) {
        if (userUuid == null || userUuid.isEmpty())
            return false;

        return true;
    }

    public boolean isValidPassword(String password) {
        if (password == null || password.isEmpty())
            return false;

        return true;
    }
}
