package com.toolkit.assetscan.controller;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.UserPo;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.common.VerifyUtil;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.redis.IRedisClient;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.StringUtils;
import com.toolkit.assetscan.service.UserManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/users")
@Api(value = "02. 用户管理接口", tags = "02-Users Manager API")
public class UserManageApi {
    private Logger logger = LoggerFactory.getLogger(UserManageApi.class);
    private final UserManageService userManageService;
    private final ResponseHelper responseHelper;
    @Autowired
    private IRedisClient redisClient;
    @Autowired
    private SystemLogsHelper systemLogs;

    @Autowired
    public UserManageApi(UserManageService userManageService, ResponseHelper responseHelper) {
        this.userManageService = userManageService;
        this.responseHelper = responseHelper;
    }

    /**
     * 2.1 添加新用户
     * @param user 用户数据
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addUser(@ModelAttribute UserPo user, BindingResult bindingResult) {
        ResponseBean response = userManageService.addUser(user);
        // 系统日志
        systemLogs.logEvent(response, "用户注册", "新增用户（账号：" + user.getAccount() + "）");

        return response;
    }

    /**
     * 2.2 获取所有用户
     * @return payload，用户记录（数组形式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllUsers() {
        return userManageService.getAllUsers();
    }

    /**
     * 2.3 根据指定的用户 账号 获取用户UUID
     * @param account 用户 账号
     * @return payload: 用户 UUID 和 账号
     */
    @RequestMapping(value = "/user-by-account", method = RequestMethod.GET)
    public @ResponseBody
    Object getUserUuidByAccount(@RequestParam("account") String account) {
        return userManageService.getUserUuidByAccount( account );
    }

    /**
     * 2.4 更新用户记录
     * @param userPo 用户记录
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object updateUser(@ModelAttribute UserPo userPo) {
        ResponseBean response = userManageService.updateUserByUuid(userPo);
        // 系统日志
        systemLogs.logEvent(response, "更新用户", "更新用户数据（账号：" + userPo.getAccount() + "）");
        return response;
    }

    /**
     * 2.5 修改用户密码
     * @param userUuid 用户 UUID
     * @param oldPwd  旧用户密码
     * @param newPwd  新用户密码
     * @return payload: 无
     */
    @RequestMapping(value = "/change-pwd", method = RequestMethod.POST)
    public @ResponseBody
    Object changePassword(
            @RequestParam("uuid") String userUuid,
            @RequestParam("old_pwd") String oldPwd,
            @RequestParam("new_pwd") String newPwd
            ) {
        ResponseBean response = userManageService.changePassword(userUuid, oldPwd, newPwd);

        // 系统日志
        systemLogs.logEvent(response, "修改密码", "用户修改密码");

        return response;
    }

    /**
     * 2.6 校验用户密码
     * @param userUuid 用户 UUID （和用户账号二选一，优先UUID）
     * @param account 用户账号
     * @param password 待校验的用户密码
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/verify-pwd", method = RequestMethod.POST)
    public synchronized @ResponseBody
    Object verifyPassword(
            @RequestParam(value = "uuid", required = false) String userUuid,
            @RequestParam(value = "account", required = false) String account,
            @RequestParam("password") String password,
            HttpServletRequest request) {
        ResponseBean resp;
        String userAccount = "";
        if ( StringUtils.isValid(userUuid) ) {
            resp = userManageService.verifyPasswordByUuid(userUuid, password);
            if (resp.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
                ResponseBean userResp = userManageService.getUserByUuid(userUuid);
                userAccount = ((UserPo)userResp.getPayload()).getAccount();
            }
        } else if ( StringUtils.isValid(account) ) {
            resp = userManageService.verifyPasswordByAccount(account, password);
            userAccount = account;
        } else {
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);
        }

        // 在 Session 中保存用户UUID和账号
        if (resp.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            request.getSession().setAttribute(Const.ACCOUNT, "");
            request.getSession().setAttribute(Const.USER_UUID, "");
        } else {
            String uuid = userManageService.accountToUuid(userAccount);
            request.getSession().setAttribute(Const.ACCOUNT, userAccount);
            request.getSession().setAttribute(Const.USER_UUID, uuid);
        }

        // 系统日志
        systemLogs.logEvent(resp, "登录", "用户登录系统（账号：" + userAccount + "）");
        if (resp.getCode() == ErrorCodeEnum.ERROR_INVALID_PASSWORD.getCode()) {
            JSONObject jsonData = (JSONObject)resp.getPayload();
            String contents = String.format("账号：%4$s，%1$s，最大密码尝试次数：%2$d，剩余次数：%3$d", resp.getError(),
                    jsonData.getIntValue("mat"), jsonData.getIntValue("rat"), userAccount);
            systemLogs.exception("登录", contents);
        } else if (resp.getCode() == ErrorCodeEnum.ERROR_USER_PASSWORD_LOCKED.getCode()) {
            JSONObject jsonData = (JSONObject)resp.getPayload();
            String contents = String.format("账号：%4$s，%1$s，最大密码尝试次数：%2$d，剩余次数：%3$d", resp.getError(),
                    jsonData.getIntValue("mat"), jsonData.getIntValue("rat"), userAccount);
            systemLogs.exception("登录", contents);
        }
        return resp;
    }

    /**
     * 2.7 根据指定的用户 UUID 查找用户记录
     * @param userUuid 用户 UUID
     * @return payload: 用户记录
     */
    @RequestMapping(value = "/user-by-uuid", method = RequestMethod.GET)
    public @ResponseBody
    Object getUserByUuid(@RequestParam("uuid") String userUuid) {
        return userManageService.getUserByUuid( userUuid );
    }

    /**
     * 2.8 激活用户
     * @param userUuid
     * @param account
     * @return
     */
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public @ResponseBody
    Object activateUser(
            @RequestParam(value = "uuid", required = false) String userUuid,
            @RequestParam(value = "account", required = false) String account,
            @RequestParam(value = "status", required = false) int status) {
        ResponseBean response;
        if ( StringUtils.isValid(userUuid) )
            response = userManageService.activateUserByUuid( userUuid, status );
        else if ( StringUtils.isValid(account) )
            response = userManageService.activateUserByAccount( account, status );
        else
            return responseHelper.error(ErrorCodeEnum.ERROR_NEED_PARAMETER);

        // 系统日志
        systemLogs.logEvent(response, "账号激活/回收", "激活/回收账号：" + account + "。");
        return response;
    }

    /**
     * 2.9 修改用户组
     * @param userUuid
     * @param userGroup
     * @return
     */
    @RequestMapping(value = "/change-user-group", method = RequestMethod.POST)
    public @ResponseBody
    Object changeUserGroup(
            @RequestParam(value = "uuid") String userUuid,
            @RequestParam(value = "user_group") int userGroup) {
        ResponseBean response = userManageService.changeUserGroup( userUuid, userGroup );

        // 系统日志
        systemLogs.logEvent(response, "用户组", "切换用户组（用户ID：" + userUuid + "）");

        return response;
    }

    /**
     * 2.10 生成动态验证码
     * @param request
     * @return
     */
    @RequestMapping(value = "/get-img-code",method = RequestMethod.GET)
    public Object getImgCode(HttpServletRequest request){
        Object[] objs = VerifyUtil.createImage();
        String randomStr = (String) objs[0];
        redisClient.set(randomStr.toLowerCase(), System.currentTimeMillis());
        return (byte[]) objs[1];
    }

    /**
     * 2.11 用户名称是否唯一
     * @param userName
     * @param userUuid  参数为空，表示全局检查名称唯一性；否则检查除自己外，其他用户是否使用该名称
     * @return
     */
    @RequestMapping(value = "/check-unique-name", method = RequestMethod.GET)
    public @ResponseBody
    Object isUserNameExist(@RequestParam("user_name") String userName,
                           @RequestParam(value = "user_uuid", required = false) String userUuid) {
        return userManageService.checkUserNameExist(userName, userUuid);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public synchronized  @ResponseBody
    Object logout(@RequestParam(value = "user_uuid") String userUuid,
                  HttpServletRequest request) {
        // 系统日志
        systemLogs.success("登出", "用户已退出系统");

        request.getSession().setAttribute(Const.ACCOUNT, "");
        request.getSession().setAttribute(Const.USER_UUID, "");

        return responseHelper.success();
    }

}
