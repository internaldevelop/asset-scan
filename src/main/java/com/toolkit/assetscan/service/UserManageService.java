package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.PasswordProps;
import com.toolkit.assetscan.bean.UserProps;
import com.toolkit.assetscan.dao.helper.UsersManageHelper;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.UserStatusEnum;
import com.toolkit.assetscan.global.params.CheckParams;
import com.toolkit.assetscan.global.params.SystemParams;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.security.VerifyHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import com.toolkit.assetscan.global.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserManageService {
    private ResponseBean responseBean;
    private final UsersManageHelper usersManageHelper;
    private final UsersMapper usersMapper;
    private final ResponseHelper responseHelper;
    private final CheckParams checkParams;
    private final VerifyHelper verifyHelper;

    public UserManageService(UsersManageHelper usersManageHelper, UsersMapper usersMapper, ResponseHelper responseHelper, CheckParams checkParams, VerifyHelper verifyHelper) {
        this.usersManageHelper = usersManageHelper;
        this.usersMapper = usersMapper;
        this.responseHelper = responseHelper;
        this.checkParams = checkParams;
        this.verifyHelper = verifyHelper;
    }

    private boolean iCheckParams(UserProps userProps) {
        responseBean = responseHelper.success();
        return true;
    }

    private ResponseBean successReturnUserInfo(String account, String userUuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("account", account);
        jsonData.put("uuid", userUuid);
        return responseHelper.success(jsonData);
    }

    /**
     * 获取所有用户
     * @return payload: 所有用户的记录
     */
    public ResponseBean getAllUsers() {
        List<UserProps> usersList = usersMapper.allUsers();
        if ( (usersList == null) || (usersList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return responseHelper.success(usersList);
    }

    /**
     * 添加新用户
     * 新用户添加后，状态为未激活，需要系统管理员审核后激活用户
     * @param userProps
     * @return payload: 账户名和系统分配的用户UUID
     */
    public ResponseBean addUser(UserProps userProps) {
        // 检查参数
        if (!iCheckParams(userProps))
            return responseBean;

        // 检查新建账户名是否已存在
        if (usersManageHelper.isUserAccount(userProps.getAccount()))
            return responseHelper.error(ErrorCodeEnum.ERROR_USERNAME_USED);

        // 设置新用户的创建时间和失效时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        userProps.setCreate_time(currentTime);
        userProps.setExpire_time(MyUtils.calculateExpireTimeStamp(currentTime, 365));

        // 分配用户UUID
        userProps.setUuid(MyUtils.generateUuid());

        // 初始化设置最大尝试次数和剩余尝试次数
        userProps.setPwd_mat(SystemParams.USER_PWD_MAT);
        userProps.setPwd_rat(SystemParams.USER_PWD_MAT);

        // 新用户状态设置为未激活
        userProps.setStatus(UserStatusEnum.USER_INACTIVE.getStatus());

        // 添加新用户的记录
        if ( !usersManageHelper.addUser(userProps) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnUserInfo(userProps.getAccount(), userProps.getUuid());
    }

    /**
     * 根据指定的用户 UUID 查找用户记录
     * @param uuid 用户 UUID
     * @return payload: 用户记录
     */
    public ResponseBean getUserByUuid(String uuid) {
        UserProps userProps = usersMapper.getUserByUuid(uuid);
        if (userProps == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 目前实现方式，返回数据有用户密码相关数据，需要做数据隐藏处理
        userProps.setPassword("********");
        userProps.setPassword_salt("********");
        return responseHelper.success(userProps);
    }

    /**
     * 根据指定的用户账号获取用户 UUID
     * @param account 用户账号
     * @return payload: 用户账号和 UUID
     */
    public ResponseBean getUserUuidByAccount(String account) {
        String userUuid = usersMapper.getUserUuidByAccount(account);
        if (!StringUtils.isValid(userUuid))
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        JSONObject jsonData = new JSONObject();
        jsonData.put("account", account);
        jsonData.put("uuid", userUuid);
        return responseHelper.success(jsonData);
    }

    /**
     * 根据用户 UUID 更新用户记录
     * @param userProps 用户记录
     * @return payload: 账户名和系统分配的用户UUID
     */
    public ResponseBean updateUserByUuid(UserProps userProps) {
        if (!iCheckParams(userProps))
            return responseBean;

        if ( !usersManageHelper.updateUserByUuid(userProps) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("account", userProps.getAccount());
        jsonData.put("uuid", userProps.getUuid());
        return responseHelper.success(jsonData);
    }

    /**
     * 修改用户密码
     * @param userUuid 用户 UUID
     * @param oldPwd  旧用户密码
     * @param newPwd  新用户密码
     * @return payload: 无
     */
    public ResponseBean changePassword(String userUuid, String oldPwd, String newPwd) {
        if (!checkParams.isValidUserUuid(userUuid) ||  !checkParams.isValidPassword(oldPwd) || !checkParams.isValidPassword(newPwd))
            return responseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);

        // 校验旧密码
        ResponseBean verifyResponse = verifyPasswordByUuid(userUuid, oldPwd);
        if (!responseHelper.isSuccess(verifyResponse))
            return verifyResponse;

        // 修改密码为新密码
        if ( !usersManageHelper.changePassword(userUuid, newPwd) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        // 获取用户密码参数
        PasswordProps passwordProps = usersMapper.getPasswordByUuid(userUuid);
        JSONObject jsonData = new JSONObject();
        jsonData.put("mat", passwordProps.getPwd_mat());
        jsonData.put("rat", passwordProps.getPwd_rat());
        return responseHelper.success(jsonData);
    }

    public ResponseBean verifyPasswordByAccount(String account, String password) {
        String userUuid = usersMapper.getUserUuidByAccount(account);
        if (!StringUtils.isValid(userUuid))
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return verifyPasswordByUuid(userUuid, password);
    }

    private ResponseBean _buildVerifyResponse(ErrorCodeEnum err, PasswordProps passwordProps) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("user_uuid", passwordProps.getUser_uuid());
        jsonData.put("mat", passwordProps.getPwd_mat());
        jsonData.put("rat", passwordProps.getPwd_rat());
        return responseHelper.error(err, jsonData);
    }

    /**
     * 校验用户密码
     * @param userUuid 用户 UUID
     * @param password 待校验的用户密码
     * @return payload: 无
     */
    public ResponseBean verifyPasswordByUuid(String userUuid, String password) {
        if (!checkParams.isValidUserUuid(userUuid) ||  !checkParams.isValidPassword(password))
            return responseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);

        // 获取用户密码参数
        PasswordProps passwordProps = usersMapper.getPasswordByUuid(userUuid);

        // 剩余尝试次数为0时，表示密码已锁定
        if (passwordProps.getPwd_rat() == 0) {
            return _buildVerifyResponse(ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED, passwordProps);
        }

        // 校验用户输入的密码
        ErrorCodeEnum errorCode = verifyHelper.verifyUserPassword(password, passwordProps.getPassword());

        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            // 校验密码失败，减密码尝试次数
            if (!usersManageHelper.decreasePasswordRAT(userUuid, passwordProps.getPwd_rat()))
                return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

            // 再检查是否已触发锁定条件
            int currentRAT = passwordProps.getPwd_rat() - 1;
            passwordProps.setPwd_rat(currentRAT);
            if ( currentRAT <= 0) {
                return _buildVerifyResponse(ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED, passwordProps);
            } else {
                return _buildVerifyResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD, passwordProps);
            }
        } else {
            // 校验成功，如果尝试次数RAT不等于MAT，则重置 RAT
            if (passwordProps.getPwd_rat() != passwordProps.getPwd_mat()) {
                if (!usersManageHelper.resetPasswordRAT(userUuid))
                    return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
                passwordProps.setPwd_rat(passwordProps.getPwd_mat());
            }
            return _buildVerifyResponse(ErrorCodeEnum.ERROR_OK, passwordProps);
        }
    }
}
