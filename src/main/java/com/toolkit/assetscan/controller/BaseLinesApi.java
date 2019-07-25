package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.AssetScanDataPo;
import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;
import com.toolkit.assetscan.dao.mybatis.ConfigCheckMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.analyze.AssetScanDataService;
import com.toolkit.assetscan.service.analyze.BaseLineTemplatesService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/baseline-check")
@Api(value = "10. 基线核查接口", tags = "10-Baselines Check API")
public class BaseLinesApi {
    @Autowired
    BaseLineTemplatesService baseLineTemplatesService;
    @Autowired
    AssetScanDataService scanDataService;
    @Autowired
    ResponseHelper responseHelper;
    @Autowired
    ConfigCheckMapper configCheckMapper;
    @Autowired
    private SystemLogsHelper systemLogs;

    /**
     * 10.1 获取指定等级的基线
     * @param level <= 0时，表示查询所有基线，>0时，查询指定等级的基线
     * @return
     */
    @RequestMapping(value = "/query-baselines", method = RequestMethod.GET)
    @ResponseBody
    Object queryBaselines(@RequestParam(value = "level", defaultValue = "-1") int level) {
        return baseLineTemplatesService.queryBaseLines(level);
    }

    /**
     * 10.2 添加资产扫描记录
     * @param scanDataPo
     * @return
     */
    @RequestMapping(value = "/add-scan-data", method = RequestMethod.POST)
    @ResponseBody
    Object addScanData(@ModelAttribute AssetScanDataPo scanDataPo) {
        return scanDataService.addScanRecord(scanDataPo);
    }

    /**
     * 10.3 查询所有的资产扫描记录
     * @param detailLevel 0: 基本记录信息；1: 联合"资产名称" 和 "IP用户姓名/账号"
     * @return
     */
    @RequestMapping(value = "/all-scan-records", method = RequestMethod.GET)
    @ResponseBody
    Object getAllScanRecords(@RequestParam(value = "detail_level", defaultValue = "0") int detailLevel) {
        if (detailLevel == 0) {
            return scanDataService.queryAllScanRecords(false);
        } else if (detailLevel == 1){
            return scanDataService.queryAllScanRecords(true);
        } else {
            return responseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);
        }
    }

    /**
     * 10.4 查询资产扫描信息（含scan_info字段）
     * @param scanUuid
     * @return
     */
    @RequestMapping(value = "/scan-info", method = RequestMethod.GET)
    @ResponseBody
    Object getScanInfo(@RequestParam(value = "scan_uuid") String scanUuid) {
        return scanDataService.getScanInfo(scanUuid);
    }

    /**
     * 10.5 新建核查结果记录
     * @param resultPo
     * @return
     */
    @RequestMapping(value = "/add-check-result", method = RequestMethod.POST)
    @ResponseBody
    Object addCheckResult(@ModelAttribute ConfigCheckResultPo resultPo) {
        return scanDataService.addCheckResult(resultPo);
    }

    /**
     * 10.6 查询核查结果
     * scanUuid 和 resultUuid 二选一，不能都为空
     * @param scanUuid
     * @param resultUuid
     * @return
     */
    @RequestMapping(value = "/check-result", method = RequestMethod.GET)
    @ResponseBody
    Object getCheckResult(@RequestParam(value = "scan_uuid", defaultValue = "") String scanUuid,
                          @RequestParam(value = "result_uuid", defaultValue = "") String resultUuid) {
        if (scanUuid != null && !scanUuid.isEmpty()) {
            return scanDataService.getCheckResultByScanUuid(scanUuid);
        } else if (resultUuid != null && !resultUuid.isEmpty()) {
            return scanDataService.getCheckResultByResultUuid(resultUuid);
        } else {
            return responseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);
        }
    }

    /**
     * 10.7 运行资产核查
     * @param assetUuid
     * @return
     */
    @RequestMapping(value = "/run-asset-check", method = RequestMethod.GET)
    @ResponseBody
    Object runScanCheck(@RequestParam(value = "asset_uuid", defaultValue = "") String assetUuid,
                          @RequestParam(value = "base_line") int baseLine) {
        ResponseBean response = scanDataService.runAssetScanCheck(assetUuid, baseLine);
        systemLogs.logEvent(response, "资产基线核查", "资产ID：" + assetUuid +
                ", 安全配置基线：" + baseLine + "级");
        return response;
    }

    /**
     * 10.8 资产扫描记录
     * @param beginTime
     * @param endTime
     * @param assetUuidList
     * @return
     */
    @RequestMapping(value = "/get-scan-records", method = RequestMethod.POST)
    public @ResponseBody
    Object getResultHistory(@RequestParam(value = "begin_time", required = false) java.sql.Timestamp beginTime,
                            @RequestParam(value = "end_time", required = false) java.sql.Timestamp endTime,
                            @RequestParam(value = "asset_uuid_list", required = false) String assetUuidList) {
        ResponseBean response = scanDataService.queryScanRecords(beginTime, endTime, assetUuidList);
        systemLogs.logEvent(response, "读取扫描记录", "记录时间范围：" + beginTime +
                "--" + endTime);
        return response;
    }

    /**
     * 10.9 初始化基线模板数据
     * @return
     */
    @RequestMapping(value = "/init-baselines", method = RequestMethod.GET)
    @ResponseBody
    Object initBaselines() {
        return baseLineTemplatesService.initBaselineTemplates();
    }

    /**
     * 10.10 生成核查报告
     * @return
     */
    @RequestMapping(value = "/get-report", method = RequestMethod.GET)
    @ResponseBody
    Object getCheckReprot(@RequestParam(value = "scan_uuid", required = false) String scanUuid) {
        ResponseBean response = scanDataService.getCheckReprot(scanUuid);
        systemLogs.logEvent(response, "生成核查报告", "系统扫描核查ID：" + scanUuid);
        return response;
    }

    /**
     * 10.11 获取资产最近一次核查统计数据
     * @param assetUuid
     * @return
     */
    @RequestMapping(value = "/asset-recent-check-stat", method = RequestMethod.GET)
    @ResponseBody
    Object getAssetRecentCheckStat(@RequestParam(value = "asset_uuid", defaultValue = "") String assetUuid) {
        ResponseBean response = scanDataService.getAssetRecentCheckStat(assetUuid);
        systemLogs.logEvent(response, "最近核查数据统计", "资产ID：" + assetUuid);
        return response;
    }

}
