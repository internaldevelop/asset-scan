package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.PasswordPo;
import com.toolkit.assetscan.bean.po.UserPo;
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

    private boolean iCheckParams(UserPo userPo) {
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
        List<UserPo> usersList = usersMapper.allUsers();
        if ( (usersList == null) || (usersList.size() == 0) )
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return responseHelper.success(usersList);
    }

    /**
     * 添加新用户
     * 新用户添加后，状态为未激活，需要系统管理员审核后激活用户
     * @param userPo
     * @return payload: 账户名和系统分配的用户UUID
     */
    public ResponseBean addUser(UserPo userPo) {
        // 检查参数
        if (!iCheckParams(userPo))
            return responseBean;

        // 检查新建账户名是否已存在
        if (usersManageHelper.isAccountExist(userPo.getAccount()))
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_REGISTERED);

        // 检查用户名是否已存在
        if (usersMapper.getExistAccountCount(userPo.getName()) > 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_USERNAME_USED);

        // 设置新用户的创建时间和失效时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        userPo.setCreate_time(currentTime);
        userPo.setExpire_time(MyUtils.calculateExpireTimeStamp(currentTime, 365));

        // 分配用户UUID
        userPo.setUuid(MyUtils.generateUuid());

        // 初始化设置最大尝试次数和剩余尝试次数
        userPo.setPwd_mat(SystemParams.USER_PWD_MAT);
        userPo.setPwd_rat(SystemParams.USER_PWD_MAT);

        // 新用户状态设置为未激活
        userPo.setStatus(UserStatusEnum.USER_INACTIVE.getStatus());

        // 添加新用户的记录
        if ( !usersManageHelper.addUser(userPo) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnUserInfo(userPo.getAccount(), userPo.getUuid());
    }

    /**
     * 根据指定的用户 UUID 查找用户记录
     * @param uuid 用户 UUID
     * @return payload: 用户记录
     */
    public ResponseBean getUserByUuid(String uuid) {
        UserPo userPo = usersMapper.getUserByUuid(uuid);
        if (userPo == null)
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        // 目前实现方式，返回数据有用户密码相关数据，需要做数据隐藏处理
        userPo.setPassword("********");
        userPo.setPassword_salt("********");
        return responseHelper.success(userPo);
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

        return successReturnUserInfo(account, userUuid);
    }
    public String accountToUuid(String account) {
        return usersMapper.getUserUuidByAccount(account);
    }

    /**
     * 根据用户 UUID 更新用户记录
     * @param userPo 用户记录
     * @return payload: 账户名和系统分配的用户UUID
     */
    public ResponseBean updateUserByUuid(UserPo userPo) {
        if (!iCheckParams(userPo))
            return responseBean;

        // 检查用户名是否已被其他账户占用
        if (usersMapper.checkNameInOtherUsers(userPo.getName(), userPo.getUuid()) > 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_USERNAME_USED);

        if ( !usersManageHelper.updateUserByUuid(userPo) )
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnUserInfo(userPo.getAccount(), userPo.getUuid());
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
        PasswordPo passwordPo = usersMapper.getPasswordByUuid(userUuid);
        JSONObject jsonData = new JSONObject();
        jsonData.put("mat", passwordPo.getPwd_mat());
        jsonData.put("rat", passwordPo.getPwd_rat());
        return responseHelper.success(jsonData);
    }

    public ResponseBean verifyPasswordByAccount(String account, String password) {
        String userUuid = usersMapper.getUserUuidByAccount(account);
        if (!StringUtils.isValid(userUuid))
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return verifyPasswordByUuid(userUuid, password);
    }

    private ResponseBean _buildVerifyResponse(ErrorCodeEnum err, PasswordPo passwordPo) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("user_uuid", passwordPo.getUser_uuid());
        jsonData.put("mat", passwordPo.getPwd_mat());
        jsonData.put("rat", passwordPo.getPwd_rat());
        jsonData.put("user_group", passwordPo.getUser_group());
        jsonData.put("email", passwordPo.getEmail());
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
        PasswordPo passwordPo = usersMapper.getPasswordByUuid(userUuid);
        if (passwordPo.getUser_status() <= 0)
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_INACTIVE);

        // 剩余尝试次数为0时，表示密码已锁定
        if (passwordPo.getPwd_rat() == 0) {
            return _buildVerifyResponse(ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED, passwordPo);
        }

        // 校验用户输入的密码
        ErrorCodeEnum errorCode = verifyHelper.verifyUserPassword(password, passwordPo.getPassword());

        if (errorCode != ErrorCodeEnum.ERROR_OK) {
            // 校验密码失败，减密码尝试次数
            if (!usersManageHelper.decreasePasswordRAT(userUuid, passwordPo.getPwd_rat()))
                return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

            // 再检查是否已触发锁定条件
            int currentRAT = passwordPo.getPwd_rat() - 1;
            passwordPo.setPwd_rat(currentRAT);
            if ( currentRAT <= 0) {
                return _buildVerifyResponse(ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED, passwordPo);
            } else {
                return _buildVerifyResponse(ErrorCodeEnum.ERROR_INVALID_PASSWORD, passwordPo);
            }
        } else {
            // 校验成功，如果尝试次数RAT不等于MAT，则重置 RAT
            if (passwordPo.getPwd_rat() != passwordPo.getPwd_mat()) {
                if (!usersManageHelper.resetPasswordRAT(userUuid))
                    return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
                passwordPo.setPwd_rat(passwordPo.getPwd_mat());
            }
            return _buildVerifyResponse(ErrorCodeEnum.ERROR_OK, passwordPo);
        }
    }

    public ResponseBean activateUserByAccount(String account, int status) {
        String userUuid = usersMapper.getUserUuidByAccount(account);
        if (!StringUtils.isValid(userUuid))
            return responseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return activateUserByUuid(userUuid, status);
    }

    public ResponseBean activateUserByUuid(String userUuid, int status) {
        int rv = usersMapper.updateStatus(userUuid, status/*UserStatusEnum.USER_ACTIVE.getStatus()*/);
        if (rv != 1)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("user_uuid", userUuid);
        jsonData.put("status", status/*UserStatusEnum.USER_ACTIVE.getStatus()*/);
        return responseHelper.success(jsonData);
    }

    public ResponseBean changeUserGroup(String userUuid, int userGroup) {
        int rv = usersMapper.updateUserGroup(userUuid, userGroup);
        if (rv != 1)
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("user_uuid", userUuid);
        jsonData.put("user_group", userGroup);
        return responseHelper.success(jsonData);
    }

    public ResponseBean checkUserNameExist(String userName, String userUuid) {
        int count;
        if ((userUuid == null) || (userUuid.isEmpty()))
            count = usersMapper.getUserNameCount(userName);
        else
            count = usersMapper.checkNameInOtherUsers(userName, userUuid);

        JSONObject jsonData = new JSONObject();
        jsonData.put("user_name", userName);
        jsonData.put("count", count);
        jsonData.put("exist", (count > 0) ? 1 : 0);
        return responseHelper.success(jsonData);
    }
}
