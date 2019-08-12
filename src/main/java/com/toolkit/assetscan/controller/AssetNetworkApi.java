package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.AssetNetworkService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/assets-network")
@Api(value = "12. 网络性能接口", tags = "12-Assets Network API")
public class AssetNetworkApi {
    private Logger logger = LoggerFactory.getLogger(AssetNetworkApi.class);
    private final AssetNetworkService assetNetworkService;
    private final ResponseHelper mResponseHelper;
    @Autowired
    AssetsMapper assetsMapper;

    @Autowired
    public AssetNetworkApi(AssetNetworkService assetNetworkService, ResponseHelper responseHelper) {
        this.assetNetworkService = assetNetworkService;
        mResponseHelper = responseHelper;
    }

    /**
     * 12.1 获取网络性能
     * @param assetUuid
     * @param type 1:延时; 2:吞吐量; 3:带宽;
     * @return
     */
    @RequestMapping(value="/delay", method = RequestMethod.GET)
    @ResponseBody
    public Object getDelayInfo(@RequestParam("asset_uuid") String assetUuid, @RequestParam("type") String type) {
        return assetNetworkService.getDelayInfo(assetUuid, type);
    }

}
