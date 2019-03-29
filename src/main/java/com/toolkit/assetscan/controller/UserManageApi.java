package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.UserProps;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import com.toolkit.assetscan.service.UserManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/users")
@Api(value = "02. 用户管理控制器", tags = "02-Users Manager API")
public class UserManageApi {
    private Logger logger = LoggerFactory.getLogger(UserManageApi.class);
    private final UserManageService userManageService;

    @Autowired
    public UserManageApi(UserManageService userManageService) {
        this.userManageService = userManageService;
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
     * 2.1 添加新用户
     * @param user 用户数据
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addUser(@ModelAttribute UserProps user, BindingResult bindingResult) {
        return userManageService.addUser(user);
    }

    /**
     * 2.3 根据指定的用户 UUID 查找用户记录
     * @param userUuid 用户 UUID
     * @return payload: 用户记录
     */
    @RequestMapping(value = "/user-by-uuid", method = RequestMethod.GET)
    public @ResponseBody
    Object getUserByUuid(@RequestParam("uuid") String userUuid) {
        logger.info("---> getUserByUuid: " + userUuid);
        return userManageService.getUserByUuid( userUuid );
    }

    /**
     * 2.4 更新用户记录
     * @param userProps 用户记录
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object updateUser(@ModelAttribute UserProps userProps) {
        return userManageService. updateUserByUuid(userProps);
    }

    /**
     * 2.5 修改用户密码
     * @param userUuid 用户 UUID
     * @param oldPwd  旧用户密码
     * @param newPwd  新用户密码
     * @return payload: 无
     */
    @RequestMapping(value = "/change-pwd", method = RequestMethod.PUT)
    public @ResponseBody
    Object changePassword(
            @RequestParam("uuid") String userUuid,
            @RequestParam("old-pwd") String oldPwd,
            @RequestParam("new-pwd") String newPwd
            ) {
        return userManageService.changePassword(userUuid, oldPwd, newPwd);
    }

    /**
     * 2.6 校验用户密码
     * @param userUuid 用户 UUID
     * @param password 待校验的用户密码
     * @return payload: 账户名和系统分配的用户UUID
     */
    @RequestMapping(value = "/verify-pwd", method = RequestMethod.POST)
    public @ResponseBody
    Object verifyPassword(@RequestParam("uuid") String userUuid, @RequestParam("password") String password) {
        return userManageService.verifyPassword( userUuid, password );
    }
}
