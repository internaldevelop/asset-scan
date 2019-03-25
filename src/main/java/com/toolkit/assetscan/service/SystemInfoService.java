package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.response.ResponseHelper;
import org.springframework.stereotype.Component;

@Component
public class SystemInfoService {
    private final ResponseHelper responseHelper;

    public SystemInfoService(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    public ResponseBean getSystemInfo() {
        JSONObject sysInfo = new JSONObject();
        sysInfo.put("sysName", "主站和终端配置检查系统");
        sysInfo.put("desc", "按照指定策略，对本机或指定远程系统进行系统安全配置核查。");
        sysInfo.put("sysVer", "1.0.0.1001");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "主站和终端配置检查系统 Bla Bla ... Bla Bla ... again ");

        return responseHelper.success(sysInfo);
    }
}
