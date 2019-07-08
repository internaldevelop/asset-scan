package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.SystemInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/sysinfo")
@Api(value = "01. 系统信息接口", tags = "01-System Info API")
public class SystemInfoApi {
    private final SystemInfoService systemInfoService;
    @Autowired
    private ResponseHelper responseHelper;

    public SystemInfoApi(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    /**
     * 1.1 获取系统版本信息
     * @return
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public @ResponseBody Object getVersion(@RequestParam("sys_type") int systemType) {
        if (systemType == 1) {
            return systemInfoService.getHostSystemInfo();
        } else if (systemType == 2) {
            return systemInfoService.getTerminalSystemInfo();
        }

        return responseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);
    }
}
