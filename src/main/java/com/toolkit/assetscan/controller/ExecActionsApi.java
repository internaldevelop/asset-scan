package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.service.ExecActionsService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/actions")
@Api(value = "07. 操作日志", tags = "07-Actions Manager API")
public class ExecActionsApi {
    private Logger logger = LoggerFactory.getLogger(TaskExecuteResultsManageApi.class);
    @Autowired private ExecActionsService execActionsService;

    /**
     * 7.1 查询所有的操作日志
     * @return :
     */
    @RequestMapping(value = "/all-exec-logs", method = RequestMethod.GET)
    public @ResponseBody
    Object queryExecActions() {
        return execActionsService.queryAllExecActions();
    }
}
