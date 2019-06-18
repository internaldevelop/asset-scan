package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.dto.SystemLogsDto;
import com.toolkit.assetscan.bean.po.SystemLogPo;
import com.toolkit.assetscan.dao.mybatis.SystemLogsMapper;
import com.toolkit.assetscan.global.response.ResponseHelper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/system-logs")
@Api(value = "09. 系统日志接口", tags = "09-System Logs API")
public class SystemLogsApi {
    @Autowired
    private SystemLogsMapper systemLogsMapper;
    @Autowired private SystemLogsHelper systemLogsHelper;
    @Autowired private ResponseHelper responseHelper;

    /**
     * 9.1 添加新的系统日志
     * @param systemLogPo
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Object addLog(@ModelAttribute SystemLogPo systemLogPo) {
        boolean rv = systemLogsHelper.addLog(systemLogPo.getType(), systemLogPo.getTitle(), systemLogPo.getContents());
        return responseHelper.success();
    }

    /**
     * 9.2 获取所有系统日志
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public Object queryAllLogs() {
        List<SystemLogsDto> logsDtos = systemLogsMapper.getAllLogs();
        return responseHelper.success(logsDtos);
    }
}
