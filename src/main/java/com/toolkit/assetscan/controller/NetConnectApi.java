package com.toolkit.assetscan.controller;

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

    public NetConnectApi(NetConnectService netconnectService) {
        this.netconnectService = netconnectService;
    }

    /**
     *12.1 网络路径连通性测试
     * @param ip 系统配置名称
     * @return payload
     */
    @RequestMapping(value = "/param", method = RequestMethod.GET)
    public @ResponseBody
    Object netConnect(@RequestParam("uuid") String uuid,
                      @RequestParam("ip") String ip) {
        //ResponseBean response = netconnectService.ping(ip);
        ResponseBean response = netconnectService.assetping(uuid,ip);

        return response;
    }

}
