package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.service.SystemInfoService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/sysinfo")
@Api(value = "01. 系统信息接口", tags = "01-System Info API")
public class SystemInfoApi {
    private final SystemInfoService systemInfoService;

    public SystemInfoApi(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    /**
     * 1.1 获取系统版本信息
     * @return
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public @ResponseBody Object getVersion() {
        return systemInfoService.getSystemInfo();
    }
}
