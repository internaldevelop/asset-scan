package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.UserProps;
import com.toolkit.assetscan.dao.mybatis.UsersMapper;
import com.toolkit.assetscan.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/users")
public class UserManageApi {
    private Logger logger = LoggerFactory.getLogger(UserManageApi.class);
    private final UserManageService userManageService;

    @Autowired
    public UserManageApi(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllUsers() {
        return userManageService.getAllUsers();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addUser(@ModelAttribute UserProps user, BindingResult bindingResult) {
        return userManageService.addUser(user);
    }

    @RequestMapping(value = "/user-by-uuid", method = RequestMethod.GET)
    public @ResponseBody
    Object getUserByUuid(@RequestParam("uuid") String uuid) {
        logger.info("---> getUserByUuid: " + uuid);
        return userManageService.getUserByUuid( uuid);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object updateUser(@ModelAttribute UserProps userProps) {
        return userManageService. updateUserByUuid(userProps);
    }
}
