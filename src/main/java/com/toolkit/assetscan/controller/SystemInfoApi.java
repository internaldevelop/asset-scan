package com.toolkit.assetscan.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping(value = "/api/sysinfo")
public class SystemInfoApi {
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public @ResponseBody Object getVersion() {
        JSONObject response = new JSONObject();
        response.put("sysName", "主站和终端配置检查系统");
        response.put("desc", "按照指定策略，对本机或指定远程系统进行系统安全配置核查。");
        response.put("sysVer", "1.0.0.1001");
        response.put("copyright", "Copyright ©2019-2022 中国电科院");
        response.put("status", "运行中");
        response.put("overview", "主站和终端配置检查系统 Bla Bla ... Bla Bla ... again ");
        return response;
    }
}
