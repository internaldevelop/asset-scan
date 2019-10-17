package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.AssetNetWorkPo;
import com.toolkit.assetscan.bean.po.AssetPerfDataPo;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetNetworkMapper;
import com.toolkit.assetscan.dao.mybatis.AssetPerfDataMapper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.common.PdfUtil;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import com.toolkit.assetscan.global.utils.StringUtils;
import com.toolkit.assetscan.service.analyze.AssetInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AssetNetworkService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper responseHelper;
    private final AssetPerfDataMapper assetPerfDataMapper;
    private final AssetNetworkMapper assetNetworkMapper;
    private final AssetInfoService assetInfoService;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private SystemLogsHelper systemLogs;

    public AssetNetworkService(AssetsMapper assetsMapper, ResponseHelper responseHelper, AssetPerfDataMapper assetPerfDataMapper, AssetNetworkMapper assetNetworkMapper, AssetInfoService assetInfoService) {
        mAssetsMapper = assetsMapper;
        this.responseHelper = responseHelper;
        this.assetPerfDataMapper = assetPerfDataMapper;
        this.assetNetworkMapper = assetNetworkMapper;
        this.assetInfoService = assetInfoService;
    }

    public ResponseBean getDelayInfo(String sourceAssetUuid, String objAssetUuid, String type) {
        AssetPo sAssetPo = mAssetsMapper.getAssetByUuid(sourceAssetUuid);  // 源资产
        if (sAssetPo == null || "".equals(sAssetPo.getIp())){
            return responseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }
        String sip = sAssetPo.getIp();

        AssetPo oAssetPo = mAssetsMapper.getAssetByUuid(objAssetUuid);  // 目的资产
        if (oAssetPo == null || "".equals(oAssetPo.getIp())){
            return responseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }
        String oip = oAssetPo.getIp();

        // 构造URL
        String url = "http://" + sip + ":8191/asset-network-info/delay?type=" + type + "&ip=" + oip;

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class);
        ResponseBean scanResponse = (ResponseBean) responseEntity.getBody();

        if (scanResponse.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
            Timestamp now = MyUtils.getCurrentSystemTimestamp();
            AssetNetWorkPo anwPo = assetNetworkMapper.getNetWorkinfo(sourceAssetUuid);
            if (anwPo == null) {
                anwPo = new AssetNetWorkPo();
                anwPo.setAsset_uuid(sourceAssetUuid);
                anwPo.setUuid(UUID.randomUUID().toString());
                anwPo.setCreate_time(now);
            }

            Map<String,String> payloadMap = (Map<String, String>) scanResponse.getPayload();
            if ("1".equals(type)) {
                anwPo.setDelay(payloadMap.get("tcp_lat"));
                anwPo.setDelay_time(now);
                systemLogs.logEvent(scanResponse, "网络延时检测", "网络延时检测");
            } else if ("2".equals(type)) {
                anwPo.setThroughput(payloadMap.get("tcp_bw_throughput"));
                anwPo.setThroughput_time(now);
                systemLogs.logEvent(scanResponse, "吞吐量检测", "吞吐量测试");
            } else if ("3".equals(type)) {
                anwPo.setBandwidth(payloadMap.get("tcp_bw"));
                anwPo.setBandwidth_time(now);
                systemLogs.logEvent(scanResponse, "通信能力检测", "通信能力测试");
            }

            assetNetworkMapper.addNetWOrkData(anwPo);

            return scanResponse;
        }
        return scanResponse;

    }

    public ResponseBean getHistoryPerfinfo(Timestamp beginTime, Timestamp endTime, String assetUuid) {
        List<AssetPerfDataPo> historyPerfList = assetPerfDataMapper.getHistoryPerfinfo(assetUuid, beginTime, endTime);
        ResponseBean response = responseHelper.success(historyPerfList);
        systemLogs.logEvent(response, "历史性能数据查询", "历史性能数据");
        return response;
    }


    public void exportPdf(HttpServletResponse response, String assetUuid) throws Exception {

        AssetPo assetInfo = mAssetsMapper.getAssetByUuid(assetUuid);
        AssetNetWorkPo anwPo = assetNetworkMapper.getNetWorkinfo(assetUuid);
        AssetPerfDataPo apInfo = assetPerfDataMapper.getAssetPerfInfo(assetUuid);

        JSONObject assetMesg = null;
        String assetIP = null;
        if ( null != assetInfo && StringUtils.isValid( assetIP = assetInfo.getIp() ) ) {
            assetMesg = assetInfoService.getAssetInfo(assetIP, "FS,Proc CPU Ranking,Proc Memory Ranking");
        }

        PdfUtil.savePerfReportPDF(response, "系统性能检查报告", assetInfo, anwPo, apInfo, assetMesg);
    }

    public void savePdf(String assetUuid) throws Exception {

        AssetPo assetInfo = mAssetsMapper.getAssetByUuid(assetUuid);
        AssetNetWorkPo anwPo = assetNetworkMapper.getNetWorkinfo(assetUuid);
        AssetPerfDataPo apInfo = assetPerfDataMapper.getAssetPerfInfo(assetUuid);

        JSONObject assetMesg = null;
        String assetIP = null;
        if ( null != assetInfo && StringUtils.isValid( assetIP = assetInfo.getIp() ) ) {
            assetMesg = assetInfoService.getAssetInfo(assetIP, "FS,Proc CPU Ranking,Proc Memory Ranking");
        }

        PdfUtil.savePerfReportPDF2("系统性能检查报告", assetInfo, anwPo, apInfo, assetMesg);
    }
}
