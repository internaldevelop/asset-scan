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

    public ResponseBean getHostSystemInfo() {
        JSONObject sysInfo = new JSONObject();
        sysInfo.put("sysName", "主站系统自动化配置核查工具");
        sysInfo.put("desc", "根据基线要求，采用定制的安全核查策略，对各类设备及系统进行安全配置核查。");
        sysInfo.put("sysVer", "1.0.0.1001");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "为完善各类设备及系统的安全性，部署安全配置检查工具，进行安全配置检查，" +
                "确保所有服务器均能够满足国家电网公司的基线要求。本系统实现了安全配置审查自动化，并可以定时审查安全配置，" +
                "动态掌握各系统最新的安全状态，及时消除安全隐患，保障信息系统的安全和稳定。");

        return responseHelper.success(sysInfo);
    }

    public ResponseBean getTerminalSystemInfo() {
        JSONObject sysInfo = new JSONObject();
        sysInfo.put("sysName", "终端系统自动化配置检测工具");
        sysInfo.put("desc", "针对采集的 Linux 系统安全配置信息，匹配三级安全配置标准模板，提取出不符合规定的安全配置。");
        sysInfo.put("sysVer", "1.0.0.201");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "采用kali2 、Java、公司自主可控 MySQL 开发，系统实现智能电网操作系统配置扫描及检测，" +
                "发现智能电网 Linux 系统的安全配置漏洞，完成智能电网 Linux 系统安全配置核查，并对检测进行定制。" +
                "系统提供告警处理，并以邮件的方式通知用户。当系统发现Linux系统漏洞、Linux系统安全配置漏洞之后，" +
                "将提供详细的报告，以邮件的方式通知用户。");

        return responseHelper.success(sysInfo);
    }

    public ResponseBean getHostPerformanceSystemInfo() {
        JSONObject sysInfo = new JSONObject();
        sysInfo.put("sysName", "主站性能测试工具");
        sysInfo.put("desc", "对目标主站设备，进行硬件参数（处理器、内存、硬盘）、网络参数进行性能测试，生成测试报告。");
        sysInfo.put("sysVer", "1.0.0.201");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "采用kali2 、Java、公司自主可控 MySQL 开发，系统实现收集设备处理器参数、内存参数、硬盘使用情况，" +
                "以及网络性能进行测试分析，完成测试报告生成、文件导出功能。");

        return responseHelper.success(sysInfo);
    }

    public ResponseBean getVulnerManageSystemInfo() {
        JSONObject sysInfo = new JSONObject();
        sysInfo.put("sysName", "电力终端系统漏洞利用工具");
        sysInfo.put("desc", "对终端设备进行完整系统的漏洞扫描，然后对扫描出的漏洞进行验证和利用。");
        sysInfo.put("sysVer", "1.0.0.201");
        sysInfo.put("copyright", "Copyright ©2019-2022 中国电科院");
        sysInfo.put("status", "运行中");
        sysInfo.put("overview", "采用kali2 、Java、Python、公司自主可控的 MySQL和 Mongo 开发，系统实现对终端设备进行完整系统的漏洞扫描，" +
                "然后对扫描出的漏洞进行利用查询以及管理功能。");

        return responseHelper.success(sysInfo);
    }
}
