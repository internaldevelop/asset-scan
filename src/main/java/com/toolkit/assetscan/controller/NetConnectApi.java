package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.PolicyPo;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.NetConnectService;
import com.toolkit.assetscan.service.SystemInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/netconnect")
@Api(value = "12. 网络路径连通性接口", tags = "12-Net Connect API")
public class NetConnectApi {
    private final NetConnectService netconnectService;
    @Autowired
    private ResponseHelper responseHelper;
    @Autowired
    private SystemLogsHelper systemLogs;

    public NetConnectApi(NetConnectService netconnectService) {
        this.netconnectService = netconnectService;
    }

    /**
     *12.1 网络路径连通性测试
     * @param ip 连通测试 IP
     * @param uuid  节点设备UUID
     * @return payload
     */
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public @ResponseBody
    Object netConnect(@RequestParam("asset_uuid") String uuid,
                      @RequestParam("ip") String ip) {
        //ResponseBean response = netconnectService.ping(ip);
        ResponseBean response = netconnectService.assetping(uuid,ip);
        systemLogs.logEvent(response, "网络路径连通性测试", "连通性测试（ip：" + ip + "）");
        return response;
    }

    /**
     * 测试指定url访问时长
     * @param url
     * @return
     */
    @RequestMapping(value = "/url-resp", method = RequestMethod.GET)
    public @ResponseBody Object urlResp(@RequestParam("asset_uuid") String assetUuid, @RequestParam("url") String url) {
        ResponseBean response = netconnectService.urlResp(assetUuid, url);
        systemLogs.logEvent(response, "测试指定url访问时长", "url访问（url：" + url + "）");
        return response;
    }





}
