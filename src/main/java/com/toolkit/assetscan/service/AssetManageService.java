package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.AssetProps;
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
        List<AssetProps> assetsList = mAssetsMapper.getAllAssets();
        if ( (assetsList == null) || (assetsList.size() == 0) )
            return mResponseHelper.error(ErrorCodeEnum.ERROR_USER_NOT_FOUND);

        return mResponseHelper.success(assetsList);
    }

    /**
     * 新建资产
     * @param assetProps
     * @return
     */
    public ResponseBean addAsset(AssetProps assetProps) {
        // 分配资产UUID
        assetProps.setUuid(MyUtils.generateUuid());

        // 添加新用户的记录
        if (mAssetsMapper.addAsset(assetProps) <= 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnAssetInfo(assetProps.getName(), assetProps.getUuid());
    }

    /**
     * 根据uuid更新资产信息
     * @param assetProps
     * @return
     */
    public ResponseBean updateAssetByUuid(AssetProps assetProps) {
        if (mAssetsMapper.updateAsset(assetProps) <= 0)
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        JSONObject jsonData = new JSONObject();
        jsonData.put("name", assetProps.getName());
        jsonData.put("uuid", assetProps.getUuid());
        return mResponseHelper.success(jsonData);
    }

    public ResponseBean deleteAsset(String assetUuid) {
        AssetProps assetProps = mAssetsMapper.getAssetByUuid(assetUuid);
        if (assetProps == null) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_POLICY_NOT_FOUND);
        }

        if (mAssetsMapper.deleteAsset(assetProps) <= 0) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        return successReturnAssetInfo(assetProps.getName(), assetProps.getUuid());
    }

    private ResponseBean successReturnAssetInfo(String name, String uuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", name);
        jsonData.put("uuid", uuid);
        return mResponseHelper.success(jsonData);
    }
}
