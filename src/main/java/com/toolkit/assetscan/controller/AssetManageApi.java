package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
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
    AssetsMapper assetsMapper;
    @Autowired
    private SystemLogsHelper systemLogs;



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
        ResponseBean response = mAssetManageService.addAsset(assetPo);
        // 系统日志
        systemLogs.logEvent(response, "新增资产", "添加新资产");
        return response;
    }

    /**
     * 6.2 获取所有资产信息
     * @return
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public synchronized Object getAllAssets() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseBean response = mAssetManageService.getAllAssets();
        return response;
    }

    /**
     * 6.3 根据uuid更新资产信息
     * @param assetPo
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Object updateUser(@ModelAttribute AssetPo assetPo) {
        ResponseBean response = mAssetManageService.updateAssetByUuid(assetPo);
        // 系统日志
        systemLogs.logEvent(response, "更新资产", "修改资产数据（ID：" + assetPo.getUuid() + "）");
        return response;
    }

    /**
     * 6.4 删除一条资产
     * @param assetUuid
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteAsset(@RequestParam("uuid") String assetUuid) {
        ResponseBean response = mAssetManageService.deleteAsset(assetUuid);
        // 系统日志
        systemLogs.logEvent(response, "删除资产", "删除资产数据（ID：" + assetUuid + "）");
        return response;
    }

    /**
     * 6.5 检查资产名称是否唯一
     * @param assetName
     * @param assetUuid 没有提供此参数，或参数为空，表示全局检查名称唯一性；否则检查除自己外，
     *                  其他资产是否使用该名称
     * @return
     */
    @RequestMapping(value = "/check-unique-name", method = RequestMethod.GET)
    @ResponseBody
    public Object isAssetNameExist(@RequestParam("asset_name") String assetName,
                                   @RequestParam(value = "asset_uuid", required = false) String assetUuid) {
        return mAssetManageService.checkAssetNameExist(assetName, assetUuid);
    }

    /**
     * 6.6 启动定时任务，采集资产的实时信息，通过websocket发送到客户端
     * @param assetUuid 资产的UUID
     * @param action  0: 停止采集；1: 开始采集
     * @param infoTypes 资产信息类别
     * @return
     */
    @RequestMapping(value = "/real-time-info", method = RequestMethod.GET)
    @ResponseBody
    public Object startRealTimeInfo(@RequestParam(value = "asset_uuid") String assetUuid,
                                    @RequestParam(value = "action") int action,
                                    @RequestParam(value = "info_types", required = false) String infoTypes) {
        if (action == 1) {
            return mAssetManageService.colletcRealTimeInfo(assetUuid, infoTypes);
        } else if (action == 0) {
            return mAssetManageService.stopRealTimeInfo(assetUuid);
        }
        return mResponseHelper.error(ErrorCodeEnum.ERROR_PARAMETER);
    }

    /**
     * 6.7 设置资产账号密码
     * @param assetUuid  资产的UUID
     * @param account  资产系统中新建账号或已有账号
     * @param password   给账号设定的新密码
     * @return
     */
    @RequestMapping(value = "/set-account-pwd", method = RequestMethod.POST)
    @ResponseBody
    Object setUserAccountPwd(@RequestParam("asset_uuid") String assetUuid,
                             @RequestParam("account") String account,
                             @RequestParam("password") String password) {
        return mAssetManageService.setAccountPassword(assetUuid, account, password);
    }

}
