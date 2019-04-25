package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetManageService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper mResponseHelper;

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
            return mResponseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return mResponseHelper.success(assetsList);
    }

    /**
     * 新建资产
     * @param assetPo
     * @return
     */
    public ResponseBean addAsset(AssetPo assetPo) {
        // 分配资产UUID
        assetPo.setUuid(MyUtils.generateUuid());

        // 添加新用户的记录
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
            return mResponseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);
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
}
