package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.service.SystemInfoService;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/sysinfo")
public class SystemInfoApi {
    private final SystemInfoService systemInfoService;

    public SystemInfoApi(SystemInfoService systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public @ResponseBody Object getVersion() {
        return systemInfoService.getSystemInfo();
    }
}
