package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.AssetScanRecordDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.bean.po.AssetScanDataPo;
import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;
import com.toolkit.assetscan.dao.mybatis.AssetScanDataMapper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.dao.mybatis.ConfigCheckMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    public ResponseBean runAssetScanCheck(String assetUuid) {
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
        scanDataPo.setCreator_uuid((String) httpServletRequest.getSession().getAttribute(Const.USER_UUID));
        scanDataPo.setScan_info(JSONObject.toJSONString(scanResponse.getPayload()));
        ResponseBean response = addScanRecord(scanDataPo);
        if (response.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            return response;
        }

        // 分析扫描信息，并保存核查结果
        return responseHelper.success();
    }
}
