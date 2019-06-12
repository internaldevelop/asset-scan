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
        sysInfo.put("sysName", "主站和终端系统自动化配置检测工具");
        sysInfo.put("desc", "根据基线要求，采用定制的安全核查策略，对各类设备及系统进行安全配置核查。");
        sysInfo.put("sysVer", "1.0.0.1001");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "为完善各类设备及系统的安全性，部署安全配置检查工具，进行安全配置检查，" +
                "确保所有服务器均能够满足国家电网公司的基线要求。本系统实现了安全配置审查自动化，并可以定时审查安全配置，" +
                "动态掌握各系统最新的安全状态，及时消除安全隐患，保障信息系统的安全和稳定。");

        return responseHelper.success(sysInfo);
    }
}
