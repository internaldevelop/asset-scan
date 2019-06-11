package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.global.common.ExcelUtil;
import com.toolkit.assetscan.global.common.HtmlUtil;
import com.toolkit.assetscan.global.common.PdfUtil;
import com.toolkit.assetscan.global.common.WordUtil;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.TaskExecuteResultsManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/tasks/results")
@Api(value = "05. 检测结果", tags = "05-Tasks results Manager API")
public class TaskExecuteResultsManageApi {
    private Logger logger = LoggerFactory.getLogger(TaskExecuteResultsManageApi.class);
    private final TaskExecuteResultsManageService taskExecuteResultsManageService;
    private final ResponseHelper responseHelper;

    @Autowired
    public TaskExecuteResultsManageApi(TaskExecuteResultsManageService taskExecuteResultsManageService, ResponseHelper responseHelper) {
        this.taskExecuteResultsManageService = taskExecuteResultsManageService;
        this.responseHelper = responseHelper;
    }

    /**
     * 5.1 任务检测结果查询
     * @return payload: 所有任务的数组 （JSON 格式）
     */
    @ApiImplicitParam(name = "taskNameIpType", value = "任务名称、目标IP、问题类型", required = true, dataType = "String",paramType="query")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody Object getAllTasks(String taskNameIpType) {

        return taskExecuteResultsManageService.getAllTasksResults(taskNameIpType);
    }

    /**
     * 任务检测结果(策略组系统数量) 统计图表数据获取 (策略数量多，故改成策略组统计)
     * @return : 任务检测结果(策略组系统数量) 统计图表数据获取
     * @return
     */
    @RequestMapping(value = "/statistics-group", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsStatisticsGroup() {
        return taskExecuteResultsManageService.getResultsStatisticsGroup();
    }

    /**
     * 5.2 任务检测结果(策略系统数量) 统计图表数据获取
     * @return : 任务检测结果(策略系统数量) 统计图表数据获取
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsStatistics() {
        return taskExecuteResultsManageService.getResultsStatistics();
    }

    /**
     * 任务检测结果(策略组数量) 统计图表数据获取(策略数量多，故改成策略组统计)
     * @return : 任务检测结果(策略组数量) 统计图表数据获取
     */
    @RequestMapping(value = "/policy-statistics-group", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsPolicieStatisticsGroup() {
        return taskExecuteResultsManageService.getResultsPolicieStatisticsGroup();
    }

    /**
     * 5.3 任务检测结果(策略数量) 统计图表数据获取
     * @return : 任务检测结果(策略数量) 统计图表数据获取
     */
    @RequestMapping(value = "/policy-statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsPolicieStatistics() {
        return taskExecuteResultsManageService.getResultsPolicieStatistics();
    }

    /**
     * 5.4 任务检测结果(系统数量) 统计图表数据获取
     * @return : 任务检测结果(系统数量) 统计图表数据获取
     */
    @RequestMapping(value = "/sys-statistics", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultsSysStatistics() {
        return taskExecuteResultsManageService.getResultsSysStatistics();
    }

    /**
     * 5.5 单个指定任务执行信息（执行状态信息和执行统计数据）
     * @param taskUuid
     * @return 返回 payload 数据，包含三部分：
     *          run_status：缓存的任务执行状态信息
     *          task_info：数据库中读取的任务信息
     *          exec_brief：执行结果摘要（忽略以下字段：task_uuid / results / create_time / exec_action_uuid ）
     */
    @RequestMapping(value = "/brief", method = RequestMethod.GET)
    public @ResponseBody
    Object getTaskExecBrief(@RequestParam("task_uuid") String taskUuid) {
        return taskExecuteResultsManageService.getTaskExecBriefInfo(taskUuid);
    }

    /**
     * 5.6 某批次策略运行结果（风险漏洞信息、策略、策略组信息）
     * @param execUuid 执行批次的 UUID。为空：所有的操作；不为空：指定操作UUID
     * @param riskLevel 风险级别。0：无风险；1~3级风险；99：所有一级以上的风险
     * @return
     */
    @RequestMapping(value = "/risks", method = RequestMethod.GET)
    public @ResponseBody
    Object getResultRisksInfo(@RequestParam("exec_action_uuid") String execUuid,
                              @RequestParam("risk_level") int riskLevel) {
        return taskExecuteResultsManageService.getResultRisksInfo(execUuid, riskLevel);
    }

    /**
     * 5.7 策略运行结果的历史记录（风险漏洞信息、策略、策略组信息）
     * @param beginTime
     * @param endTime
     * @param policyUuidList
     * @param scanResult
     * @return
     */
    @RequestMapping(value = "/history", method = RequestMethod.POST)
    public @ResponseBody
    Object getResultHistory(@RequestParam(value = "begin_time", required = false) java.sql.Timestamp beginTime,
                            @RequestParam(value = "end_time", required = false) java.sql.Timestamp endTime,
                            @RequestParam(value = "policy_uuid_list", required = false) String policyUuidList,
                            @RequestParam(value = "scan_result", required = false) String scanResult) {
        return taskExecuteResultsManageService.queryResultsHistory(beginTime, endTime, policyUuidList, scanResult);
    }

    /**
     * 5.9 获取IEE漏洞数（测试第二数据源）
     * @return
     */
    @RequestMapping(value = "/all-iie-vul", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllIieVulInfo() {
        return taskExecuteResultsManageService.getAllIieVulInfo();
    }

    /**
     * 5.10 任务检测结果 - 导出Excel/Word/Pdf/Html
     * @param response
     */
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskNameIpType", value = "任务名称、目标IP、问题类型", required = true, dataType = "String",paramType="query"),
        @ApiImplicitParam(name = "type", value = "导出格式", required = true, dataType = "String",paramType="query")
    })
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportExcel(HttpServletResponse response, String taskNameIpType, String type){
        List<TaskResultsDto> list =  taskExecuteResultsManageService.getTasksResultsList(taskNameIpType);
        ExcelDataDto data = new ExcelDataDto();
        data.setName("任务检测结果");
        List<String> titles = new ArrayList();
        titles.add("任务号");
        titles.add("任务名称");
        titles.add("检测目标");
        titles.add("目标IP");
        titles.add("问题类型");
        titles.add("危害等级");
        titles.add("问题描述");
        titles.add("建议方案");
        data.setTitles(titles);

        List<List<Object>> rows = new ArrayList();
        for (TaskResultsDto trDto : list) {
            List<Object> row = new ArrayList();
            row.add(trDto.getTask_id());  // 任务号
            row.add(trDto.getTask_name());  // 任务名称
            row.add(trDto.getAssets_name());  // 检测目标
            row.add(trDto.getAssets_ip());  // 目标IP
            row.add(trDto.getPolicy_name());  // 问题类型
            row.add(trDto.getRisk_level());  // 危害等级
            row.add(trDto.getDescription());  // 问题描述
            row.add(trDto.getSolutions());  // 建议方案
            rows.add(row);
        }
        data.setRows(rows);

        try{
            if ("Word".equals(type)) {
                WordUtil.exportWord(response,"任务检测结果", data);
            } else if ("Pdf".equals(type)) {
                PdfUtil.exportPdf(response,"任务检测结果",data);
            } else if ("Html".equals(type)) {
                HtmlUtil.exportHtml(response,"任务检测结果",data);
            } else {
                ExcelUtil.exportExcel(response,"任务检测结果",data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//    @Autowired
//    private MsgProducer msgProducer;
//
//    /**
//     * 发送消息 - Test
//     * @return
//     */
//    @RequestMapping(value = "/send-msg", method = RequestMethod.GET)
//    public @ResponseBody
//    Object SendMsg() {
//
//        for (int i=0; i< 10; i++) {
//            msgProducer.sendMsg("发送消息"+ Math.random());
//        }
//        return null;
//    }


}
