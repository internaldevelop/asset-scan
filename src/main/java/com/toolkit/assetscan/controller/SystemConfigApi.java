package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.SystemConfigPo;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.SystemConfigService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/system-config")
@Api(value = "11. 系统配置接口", tags = "11-System Config API")
public class SystemConfigApi {
    private Logger logger = LoggerFactory.getLogger(SystemConfigApi.class);
    private final SystemConfigService mSystemConfigService;
    private final ResponseHelper responseHelper;
    @Autowired
    private SystemLogsHelper systemLogs;

    @Autowired
    public SystemConfigApi(SystemConfigService systemConfigService, ResponseHelper responseHelper) {
        this.mSystemConfigService = systemConfigService;
        this.responseHelper = responseHelper;
    }

    /**
     * 11.1 添加一条新的系统配置
     * @param systemConfigPo 系统配置数据
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 系统配置名称和值
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addSystemConfig(@ModelAttribute SystemConfigPo systemConfigPo, BindingResult bindingResult) {
        ResponseBean response = mSystemConfigService.addSystemConfig(systemConfigPo);
        // 增加一条系统配置
        systemLogs.logEvent(response, "增加一条系统配置", "系统配置：" + systemConfigPo.getName() + ", " + systemConfigPo.getValue() + "）");
        return response;
    }

    /**
     * 11.2 获取所有系统配置
     * @return payload，数组形式
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllSystemConfigs() {
        return mSystemConfigService.getAllSystemConfigs();
    }

    /**
     * 11.3 更新系统配置
     * @param systemConfigPo 系统配置
     * @return payload
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object updateSystemConfig(@ModelAttribute SystemConfigPo systemConfigPo) {
        ResponseBean response = mSystemConfigService.updateSystemConfig(systemConfigPo);
        // 系统日志
        systemLogs.logEvent(response, "更新系统配置", "更新系统配置：" + systemConfigPo.getName() + ", " + systemConfigPo.getValue() + "）");
        return response;
    }

    /**
     * 11.4 删除系统配置
     * @param name 系统配置名称
     * @return payload
     */
    @RequestMapping(value = "/config-by-name", method = RequestMethod.POST)
    public @ResponseBody
    Object getSystemConfigByName(@RequestParam("name") String name) {
        ResponseBean response = mSystemConfigService.getSystemConfigByName(name);
        // 系统日志
        systemLogs.logEvent(response, "获取系统配置", "系统配置名称：" + name + "）");
        return response;
    }

    /**
     * 11.5 删除系统配置
     * @param name 系统配置名称
     * @return payload
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    Object deleteSystemConfig(@RequestParam("name") String name) {
        ResponseBean response = mSystemConfigService.deleteSystemConfig(name);
        // 系统日志
        systemLogs.logEvent(response, "删除系统配置", "系统配置名称：" + name + "）");
        return response;
    }
}
