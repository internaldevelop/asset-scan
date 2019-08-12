package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import com.toolkit.assetscan.global.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AssetNetworkService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper mResponseHelper;

    @Autowired
    RestTemplate restTemplate;

    public AssetNetworkService(AssetsMapper assetsMapper, ResponseHelper responseHelper) {
        mAssetsMapper = assetsMapper;
        mResponseHelper = responseHelper;
    }

    public ResponseBean getDelayInfo(String sourceAssetUuid, String objAssetUuid, String type) {
        AssetPo sAssetPo = mAssetsMapper.getAssetByUuid(sourceAssetUuid);  // 源资产
        if (sAssetPo == null && "".equals(sAssetPo.getIp())){
            return null;
        }
        String sip = sAssetPo.getIp();

        AssetPo oAssetPo = mAssetsMapper.getAssetByUuid(objAssetUuid);  // 目的资产
        if (oAssetPo == null && "".equals(oAssetPo.getIp())){
            return null;
        }
        String oip = oAssetPo.getIp();

        // 构造URL
        String url = "http://" + sip + ":8191/asset-network-info/delay?type=" + type + "&ip=" + oip;

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class);
        ResponseBean scanResponse = (ResponseBean) responseEntity.getBody();
        if (scanResponse.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            return scanResponse;
        }
        return scanResponse;

    }

}
