package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.AssetManageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/assets")
@Api(value = "06. 资产管理接口", tags = "06-Assets Manager API")
public class AssetManageApi {
    private Logger logger = LoggerFactory.getLogger(AssetManageApi.class);
    private final AssetManageService mAssetManageService;
    private final ResponseHelper mResponseHelper;

    @Autowired
    public AssetManageApi(AssetManageService assetManageService, ResponseHelper responseHelper) {
        mAssetManageService = assetManageService;
        mResponseHelper = responseHelper;
    }

    /**
     * 6.1 添加新资产
     * @param assetPo
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Object addUser(@ModelAttribute AssetPo assetPo) {
        return mAssetManageService.addAsset(assetPo);
    }

    /**
     * 6.2 获取所有资产信息
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public Object getAllAssets() {
        return mAssetManageService.getAllAssets();
    }

    /**
     * 6.3 根据uuid更新资产信息
     * @param assetPo
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Object updateUser(@ModelAttribute AssetPo assetPo) {
        return mAssetManageService.updateAssetByUuid(assetPo);
    }

    /**
     * 6.4 删除一条资产
     * @param assetUuid
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteAsset(@RequestParam("uuid") String assetUuid) {
        return mAssetManageService.deleteAsset(assetUuid);
    }

}
