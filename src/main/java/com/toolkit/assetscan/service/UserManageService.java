package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.UserProps;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManageService {
    private ResponseBean responseBean;
    private final UsersMapper usersMapper;
    private final ResponseHelper responseHelper;

    public UserManageService(UsersMapper usersMapper, ResponseHelper responseHelper) {
        this.usersMapper = usersMapper;
        this.responseHelper = responseHelper;
    }

    private boolean checkParams(UserProps userProps) {
        responseBean = responseHelper.success();
        return true;
    }

    public ResponseBean getAllUsers() {
        List<UserProps> usersList = usersMapper.allUsers();
        if ( (usersList == null) || (usersList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return responseHelper.success(usersList);
    }

    public ResponseBean addUser(UserProps userProps) {
        if (!checkParams(userProps))
            return responseBean;

        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        userProps.setCreate_time(currentTime);
        userProps.setExpire_time(currentTime);

        String uuid = MyUtils.generateUuid();
        userProps.setUuid(uuid);

        if (usersMapper.addUser(userProps) != 1)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("account", userProps.getAccount());
        jsonData.put("uuid", userProps.getUuid());
        return responseHelper.success(jsonData);
    }

    public ResponseBean getUserByUuid(String uuid) {
        UserProps userProps = usersMapper.getUserByUuid(uuid);
        if (userProps == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        userProps.setPassword("********");
        userProps.setPassword_salt("********");
        return responseHelper.success(userProps);
    }

    public ResponseBean updateUserByUuid(UserProps userProps) {
        if (!checkParams(userProps))
            return responseBean;

        if (usersMapper.updateUserByUuid(userProps) != 1)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("account", userProps.getAccount());
        jsonData.put("uuid", userProps.getUuid());
        return responseHelper.success(jsonData);
    }
}
