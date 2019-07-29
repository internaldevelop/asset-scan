package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.AssetAccountDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Component
public class AssetManageService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper mResponseHelper;
    @Autowired
    AssetCollectScheduler assetCollectScheduler;
    @Autowired
    RestTemplate restTemplate;

    public AssetManageService(AssetsMapper assetsMapper, ResponseHelper responseHelper) {
        mAssetsMapper = assetsMapper;
        mResponseHelper = responseHelper;
    }

    /**
     * 获取所有资产
     * @return
     */
    public ResponseBean getAllAssets() {
        List<AssetPo> assetsList = mAssetsMapper.getAllAssets();
        if ( (assetsList == null) || (assetsList.size() == 0) )
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_EMPTY);

        return mResponseHelper.success(assetsList);
    }

    /**
     * 新建资产
     * @param assetPo
     * @return
     */
    public ResponseBean addAsset(AssetPo assetPo) {
        // 添加资产时，不允许使用系统内已存在的资产名称
        if (mAssetsMapper.getAssetNameCount(assetPo.getName()) > 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NAME_EXIST);

        // 分配资产UUID
        assetPo.setUuid(MyUtils.generateUuid());

        // 添加新资产的记录
        if (mAssetsMapper.addAsset(assetPo) <= 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnAssetInfo(assetPo.getName(), assetPo.getUuid());
    }

    /**
     * 根据uuid更新资产信息
     * @param assetPo
     * @return
     */
    public ResponseBean updateAssetByUuid(AssetPo assetPo) {
        // 更新资产时，不允许使用其他资产已采用的名称
        if (mAssetsMapper.checkNameInOtherAssets(assetPo.getName(), assetPo.getUuid()) > 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NAME_EXIST);

        if (mAssetsMapper.updateAsset(assetPo) <= 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("name", assetPo.getName());
        jsonData.put("uuid", assetPo.getUuid());
        return mResponseHelper.success(jsonData);
    }

    public ResponseBean deleteAsset(String assetUuid) {
        AssetPo assetPo = mAssetsMapper.getAssetByUuid(assetUuid);
        if (assetPo == null) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }

        if (mAssetsMapper.deleteAsset(assetPo) <= 0) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        return successReturnAssetInfo(assetPo.getName(), assetPo.getUuid());
    }

    private ResponseBean successReturnAssetInfo(String name, String uuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", name);
        jsonData.put("asset_uuid", uuid);
        return mResponseHelper.success(jsonData);
    }

    public ResponseBean checkAssetNameExist(String assetName, String assetUuid) {
        int count;
        if ((assetUuid == null) || (assetUuid.isEmpty()))
            count = mAssetsMapper.getAssetNameCount(assetName);
        else
            count = mAssetsMapper.checkNameInOtherAssets(assetName, assetUuid);

        JSONObject jsonData = new JSONObject();
        jsonData.put("asset_name", assetName);
        jsonData.put("count", count);
        jsonData.put("exist", (count > 0) ? 1 : 0);
        return mResponseHelper.success(jsonData);
    }

    public ResponseBean colletcRealTimeInfo(String assetUuid, String infoTypes) {
        AssetPo assetPo = mAssetsMapper.getAssetByUuid(assetUuid);
        if (assetPo == null)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);

        if (assetCollectScheduler.setTask(assetUuid, assetPo.getIp(), infoTypes)){
            return mResponseHelper.success();
        }
        return mResponseHelper.error(ErrorCodeEnum.ERROR_FAILED_ASSET_RTINFO);
    }

    public ResponseBean stopRealTimeInfo(String assetUuid) {
        assetCollectScheduler.stopTask(assetUuid);
        return mResponseHelper.success();
    }

    public ResponseBean setAccountPassword(String assetUuid, String account, String password) {
        // 获取资产信息
        AssetPo assetPo = mAssetsMapper.getAssetByUuid(assetUuid);
        if (assetPo == null) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }

        // 构造URL
        String ip = "http://" + assetPo.getIp() + ":8191";
        String url = ip + "/asset-sec-cfg/set-user-pwd";

        // 构造参数map：使用RestTemplate发送multipart/form-data格式的数据
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("asset_uuid", assetUuid);
        map.add("account", account);
        map.add("password", password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.postForEntity(url, request, ResponseBean.class);
        ResponseBean scanResponse = (ResponseBean) responseEntity.getBody();
        if (scanResponse.getCode() != ErrorCodeEnum.ERROR_OK.getCode()) {
            return scanResponse;
        }

        return mResponseHelper.success();
    }

}
