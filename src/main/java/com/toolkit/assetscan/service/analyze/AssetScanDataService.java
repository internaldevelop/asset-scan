package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.AssetScanRecordDto;
import com.toolkit.assetscan.bean.dto.CheckStatisticsDto;
import com.toolkit.assetscan.bean.dto.ExcelDataDto;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.po.*;
import com.toolkit.assetscan.dao.mybatis.*;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.common.PdfUtil;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import com.toolkit.assetscan.global.utils.SystemUtils;
import com.toolkit.assetscan.global.websocket.SockMsgTypeEnum;
import com.toolkit.assetscan.global.websocket.WebSocketServer;
import com.toolkit.assetscan.service.MailManageService;
import com.toolkit.assetscan.service.analyze.AnalyzeSubject;
import com.toolkit.assetscan.service.analyze.StartupConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class AssetScanDataService {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    ResponseHelper responseHelper;
    @Autowired
    AssetScanDataMapper assetScanDataMapper;
    @Autowired
    ConfigCheckMapper configCheckMapper;
    @Autowired
    AssetsMapper assetsMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BaseLineMapper baseLineMapper;
    @Autowired
    SystemConfigsMapper systemConfigsMapper;
    @Autowired
    UsersMapper usersMapper;
    @Autowired
    private MailManageService mailManageService;

    // 保存参数用于新建核查结果记录
    @Autowired
    AnalyzeSubject resultOper;

    @Autowired
    StartupConfig startupConfig;
    @Autowired
    AccountConfig accountConfig;
    @Autowired
    PasswordConfig passwordConfig;
    @Autowired
    ServiceConfig serviceConfig;
    @Autowired
    LoginConfig loginConfig;
    @Autowired
    SysLogConfig sysLogConfig;
    @Autowired
    IptablesConfig iptablesConfig;

    public ResponseBean addScanRecord(AssetScanDataPo scanDataPo) {
        // 设置扫描记录的 UUID 和创建时间
        scanDataPo.setUuid(MyUtils.generateUuid());
        scanDataPo.setCreate_time(MyUtils.getCurrentSystemTimestamp());

        if (assetScanDataMapper.addScanData(scanDataPo) == 1) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", scanDataPo.getUuid());
            return responseHelper.success(jsonObject);
        } else {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }
    }

    public ResponseBean queryAllScanRecords(boolean joinQuery) {
        if (joinQuery) {
            List<AssetScanRecordDto> recordDtos = assetScanDataMapper.getAllScanRecordData();
            if (recordDtos == null) {
                return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
            } else {
                return responseHelper.success(recordDtos);
            }
        } else {
            List<AssetScanDataPo> scanDataPos = assetScanDataMapper.getAllScanRecords();
            if (scanDataPos == null) {
                return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
            } else {
                return responseHelper.success(scanDataPos);
            }
        }
    }

    public ResponseBean queryScanRecords(java.sql.Timestamp beginTime,
                                         java.sql.Timestamp endTime,
                                         String assetUuidList) {
        List<AssetScanRecordDto> recordDtos = assetScanDataMapper.getAllScanRecordData();
        if (recordDtos == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
        } else {
            java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
            // 未提供起始时间，按1970年时间算起
            if (beginTime == null) {
                beginTime = new java.sql.Timestamp(0);
            }
            // 未提供结束时间，用当前时间处理
            if (endTime == null) {
                endTime = currentTime;
            }
            // 检验起止时间是否历史时间
            if ( beginTime.after(currentTime)|| endTime.after(currentTime) )
                return responseHelper.error(ErrorCodeEnum.ERROR_TIME_AFTER_CURRENT);

            // 起始时间不能晚于结束时间
            if ( beginTime.after(endTime))
                return responseHelper.error(ErrorCodeEnum.ERROR_TIME_INCORRECT);

            // 如果策略 uuid 列表未提供，则用空字符串表示全部资产
            if (assetUuidList == null)
                assetUuidList = "";

            List<AssetScanRecordDto> results = new ArrayList<>();
            for(AssetScanRecordDto scanRecord: recordDtos) {
                if ((assetUuidList.contains(scanRecord.getAsset_uuid()) || assetUuidList.equals(""))
                    && (scanRecord.getCreate_time().after(beginTime) && scanRecord.getCreate_time().before(endTime))) {
                    results.add(scanRecord);
                }
            }
            return responseHelper.success(results);
        }
    }

    public ResponseBean getScanInfo(String scanUuid) {
        AssetScanRecordDto scanInfo = assetScanDataMapper.getScanInfo(scanUuid);
        if (scanInfo == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
        } else {
            return responseHelper.success(scanInfo);
        }
    }

    public ResponseBean addCheckResult(ConfigCheckResultPo resultPo) {
        // 设置核查结果的 UUID 和创建时间
        resultPo.setUuid(MyUtils.generateUuid());
        resultPo.setCreate_time(MyUtils.getCurrentSystemTimestamp());

        if (configCheckMapper.addCheckResult(resultPo) == 1) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uuid", resultPo.getUuid());
            return responseHelper.success(jsonObject);
        } else {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }
    }

    public ResponseBean getCheckResultByScanUuid(String scanUuid) {
        List<ConfigCheckResultPo> resultPos = configCheckMapper.getScanResults(scanUuid);
        if (resultPos == null || resultPos.size() == 0) {
            return responseHelper.error(ErrorCodeEnum.ERROR_CHECK_RESULT_NOT_FOUND);
        } else {
            return responseHelper.success(resultPos);
        }
    }

    public ResponseBean getCheckResultByResultUuid(String resultUuid) {
        ConfigCheckResultPo resultPo = configCheckMapper.getResult(resultUuid);
        if (resultPo == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_CHECK_RESULT_NOT_FOUND);
        } else {
            return responseHelper.success(resultPo);
        }
    }

    public ResponseBean getAssetRecentScanRecord(String assetUuid) {
        AssetScanRecordDto scanRecordDto = assetScanDataMapper.getAssetRecentScanInfo(assetUuid);
        if (scanRecordDto == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
        } else {
            return responseHelper.success(scanRecordDto);
        }
    }

    public ResponseBean getAssetRecentCheckStat(String assetUuid) {
        // 获取资产的最近一条扫描记录
        AssetScanRecordDto scanRecordDto = assetScanDataMapper.getAssetRecentScanInfo(assetUuid);
        if (scanRecordDto == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
        }

        // 获取最近一次扫描的核查统计数据
        List<CheckStatisticsDto> statisticsDtos = baseLineMapper.getCheckStatics(scanRecordDto.getUuid());
        if (statisticsDtos == null || statisticsDtos.size() == 0) {
            return responseHelper.error(ErrorCodeEnum.ERROR_CHECK_RESULT_NOT_FOUND);
        }

        JSONObject result = new JSONObject();
        result.put("recent", scanRecordDto);
        result.put("statistics", statisticsDtos);

        return responseHelper.success(result);
    }

    public ResponseBean runAssetScanCheck(String assetUuid, int baseLine) {
        AssetPo assetPo = assetsMapper.getAssetByUuid(assetUuid);
        // 构造URL
        String ip = "http://" + assetPo.getIp() + ":8191";
        String url = ip + "/asset-sec-cfg/acquire?types={types}";

        // 构造参数map
        HashMap<String, String> map = new HashMap<>();
        map.put("types", "");

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
        ResponseBean scanResponse = (ResponseBean)responseEntity.getBody();
        if (scanResponse.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            return scanResponse;
        }

        // 保存扫描信息
        AssetScanDataPo scanDataPo = new AssetScanDataPo();
        scanDataPo.setAsset_uuid(assetUuid);
        scanDataPo.setBase_line(baseLine);
        scanDataPo.setCreator_uuid((String) httpServletRequest.getSession().getAttribute(Const.USER_UUID));
        scanDataPo.setScan_info(JSONObject.toJSONString(scanResponse.getPayload()));
        ResponseBean response = addScanRecord(scanDataPo);
        if (response.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            return response;
        }

        // 分析扫描信息，并保存核查结果
        resultOper.setBaseLine(baseLine);
        resultOper.setAssetUuid(assetUuid);
        resultOper.setScanUuid(scanDataPo.getUuid());
        return checkScanData("", scanDataPo.getScan_info(), baseLine);
    }

    public ResponseBean checkScanData(String scanUuid, String scanInfo, int baseLine) {
        // 优先从scanUuid查询scanInfo
        AssetScanDataPo scanDataPo;
        if (scanUuid != null && !scanUuid.isEmpty()) {
            scanDataPo = assetScanDataMapper.getScanInfo(scanUuid);
            if (scanDataPo == null) {
                return responseHelper.error(ErrorCodeEnum.ERROR_SCAN_NOT_FOUND);
            }
            scanInfo = scanDataPo.getScan_info();
        }

        // 检查 scanInfo
        JSONObject jsonScanInfo;
        if (scanInfo == null || scanInfo.isEmpty()) {
            return responseHelper.error(ErrorCodeEnum.ERROR_INVALID_SCAN_INFO);
        } else {
            jsonScanInfo = JSONObject.parseObject(scanInfo);
            if (jsonScanInfo == null) {
                return responseHelper.error(ErrorCodeEnum.ERROR_INVALID_SCAN_INFO);
            }
        }

        // 获取基线参数
        BaseLinePo baseLinePo = baseLineMapper.getBaseLine(baseLine);
        if (baseLinePo == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
        }
        JSONObject jsonBaseline = JSONObject.parseObject(baseLinePo.getTemplates());
        if (jsonBaseline == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
        }

        ResponseBean responseCheckScanInfo = checkScanInfo(jsonScanInfo, jsonBaseline);

        if (scanUuid != null && !scanUuid.isEmpty()) {
            List<ConfigCheckResultPo> resultPos = configCheckMapper.getScanResults(scanUuid);
            if (resultPos != null || resultPos.size() > 0) {
                // 邮件通知核查结果
                sendCheckScanMail(resultPos);
            }
        }
        return responseCheckScanInfo;
    }

    public ResponseBean getCheckReprot(String scanUuid) {
        List<ConfigCheckResultPo> resultPos = configCheckMapper.getScanResults(scanUuid);
        sendCheckScanMail(resultPos);
        return responseHelper.success();
    }

    private void sendCheckScanMail(List<ConfigCheckResultPo> resultPos) {
        String fileTitle = "资产扫描核查结果";
        int riskCount = 0;
        AssetPo assetPo = null;
        for(ConfigCheckResultPo configCheckResultPo: resultPos) {
            if (configCheckResultPo.getRisk_level() > 0) {
                riskCount++;
                if (assetPo == null) {
                    assetPo = assetsMapper.getAssetByUuid(configCheckResultPo.getAsset_uuid());
                }
            }
        }
        try{
            if (riskCount > 0) {
                SystemConfigPo systemConfigPo = systemConfigsMapper.getSystemConfigByName("mail-to-user-on-off");
                if (systemConfigPo != null && systemConfigPo.getValue().equals("on")) {
                    String userUuid = (String) httpServletRequest.getSession().getAttribute(Const.USER_UUID);
                    String account = (String) httpServletRequest.getSession().getAttribute(Const.ACCOUNT);
                    UserPo userPo = usersMapper.getUserByUuid(userUuid);
                    String email = null;
                    if (userPo != null) {
                        email = userPo.getEmail();
                    }
                    if (email != null) {
                        //getAssetInfo("127.0.0.1")
                        String pathName = PdfUtil.saveReportPDF(fileTitle, account, resultPos, riskCount, assetPo, getAssetInfo(assetPo.getIp()));
                        String content = "详情请查看附件。";
                        mailManageService.sendSimpleTextMail(fileTitle,content,email, pathName);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private JSONObject getAssetInfo(String assetIP) {
        // 构造URL
        String ip = "http://" + assetIP + ":8191";
        String url = ip + "/asset-info/acquire?types={types}";

        // 构造参数map
        HashMap<String, String> map = new HashMap<>();
        map.put("types", "CPU,Mem,Net Config");

        // 向节点发送请求，并返回节点的响应结果
        try{
            ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
            ResponseBean responseBean = (ResponseBean)responseEntity.getBody();
            if (responseBean.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
                JSONObject jsonMsg = (JSONObject)JSONObject.toJSON(responseBean.getPayload());
                return jsonMsg;
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }


    /**
     * baseline 格式如下：
     * {
     *     startup: {
     *         SELinux_status: "检查SELinux是否开启",
     *         SELinux_mode: "检查SELinux模式是否为enforcing",
     *         SELinux_policy: "检查SELinux策略是否为strict",
     *         ......
     *     }
     * }
     * @param scanInfo
     * @param baseLine
     * @return
     */
    private ResponseBean checkScanInfo(JSONObject scanInfo, JSONObject baseLine) {
        // 检查 SELinux
        if (!startupConfig.checkSELinux(scanInfo.getJSONObject("SELinux"), baseLine.getJSONObject("startup"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查自启动服务
        if (!startupConfig.checkSelfRunServices(scanInfo.getJSONArray("selfRunServices"), baseLine.getJSONObject("startup"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查账号配置
        if (!accountConfig.checkAccounts(scanInfo.getJSONArray("accounts"), baseLine.getJSONObject("accounts"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查账户组配置
        if (!accountConfig.checkGroupPwd(scanInfo.getJSONArray("groups"), baseLine.getJSONObject("accounts"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查密码配置
        if (!passwordConfig.checkPassword(scanInfo.getJSONObject("passwordProps"), baseLine.getJSONObject("passowrd"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查服务安全配置
        if (!serviceConfig.checkService(scanInfo, baseLine.getJSONObject("services"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查登录安全配置
        if (!loginConfig.checkLogin(scanInfo.getJSONObject("login"), baseLine.getJSONObject("login"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查系统日志安全配置
        if (!sysLogConfig.checkSysLog(scanInfo.getJSONObject("syslog"), baseLine.getJSONObject("syslog"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }

        // 检查iptables配置
        if (!iptablesConfig.checkIptables(scanInfo.getJSONObject("iptablesConfig"), baseLine.getJSONObject("iptables"))) {
            return responseHelper.error(ErrorCodeEnum.ERROR_FAIL_CHECK_SCAN_INFO);
        }
        return responseHelper.success();
    }
}
