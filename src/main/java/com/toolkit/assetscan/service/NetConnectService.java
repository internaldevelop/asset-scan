package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.AssetNetWorkPo;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetNetworkMapper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.dao.helper.TasksManageHelper;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Component
public class NetConnectService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper mResponseHelper;
    private final TasksMapper mtasksMapper;
    private final TasksManageHelper mtasksManageHelper;
    private final AssetNetworkMapper assetNetworkMapper;

    @Autowired
    AssetCollectScheduler assetCollectScheduler;
    @Autowired
    RestTemplate restTemplate;

    public NetConnectService(TasksMapper tasksMapper, TasksManageHelper tasksManageHelper, AssetsMapper assetsMapper, ResponseHelper responseHelper, AssetNetworkMapper assetNetworkMapper) {
        mtasksMapper = tasksMapper;
        mtasksManageHelper = tasksManageHelper;
        mAssetsMapper = assetsMapper;
        mResponseHelper = responseHelper;
        this.assetNetworkMapper = assetNetworkMapper;
    }

    private ResponseBean successPing(String ip, boolean isconnect) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("ip", ip);
        jsonData.put("isconnect", isconnect);
        return mResponseHelper.success(jsonData);
    }

    /**
     * 网络路径连通性测试
     * @param ip
     * @return
     */
    public ResponseBean ping(String ip) {

        boolean connect = false;
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec("ping " + ip);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println("返回值为:"+sb);
            is.close();
            isr.close();
            br.close();

            if (null != sb && !sb.toString().equals("")) {
                String logString = "";
                if (sb.toString().indexOf("TTL") > 0) {
                    // 网络畅通
                    connect = true;
                } else {
                    // 网络不畅通
                    connect = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return successPing(ip, connect);
    }


    /**
     * */
    /**
     * 调用节点 网络路径连通性测试
     * @param ip
     * @return
     */
    public ResponseBean assetping(String uuid,String ip) {
        AssetPo assetPo = mAssetsMapper.getAssetByUuid(uuid);
        if (assetPo == null || "".equals(assetPo.getIp())) {
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }

        // 构造URL
        String strip = "http://" + assetPo.getIp() + ":8191";
        String url = strip + "/netconnect/param?ip="+ip;

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class);

        JSONObject jsonData = new JSONObject();

        JSONObject jsonMsg = (JSONObject)JSONObject.toJSON(responseEntity.getBody());

        JSONObject payloadObj = jsonMsg.getJSONObject("payload");
        jsonData.put("isconnect", payloadObj.getString("isconnect"));

        Timestamp now = MyUtils.getCurrentSystemTimestamp();
        AssetNetWorkPo anwPo = assetNetworkMapper.getNetWorkinfo(uuid);
        if (anwPo == null) {
            anwPo = new AssetNetWorkPo();
            anwPo.setAsset_uuid(uuid);
            anwPo.setUuid(UUID.randomUUID().toString());
            anwPo.setCreate_time(now);
        }

        anwPo.setConnect_ip(ip);
        anwPo.setConnect_flag(payloadObj.getString("isconnect"));
        anwPo.setConnect_time(now);

        assetNetworkMapper.addNetWOrkData(anwPo);

        return mResponseHelper.success(jsonData);
    }


    public ResponseBean urlResp(String assetUuid, String tUrl) {
        AssetPo assetPo = mAssetsMapper.getAssetByUuid(assetUuid);  // 源资产
        if (assetPo == null || "".equals(assetPo.getIp())){
            return mResponseHelper.error(ErrorCodeEnum.ERROR_ASSET_NOT_FOUND);
        }

        // 构造URL
        String url = "http://" + assetPo.getIp() + ":8191/netconnect/url-resp?url=" + tUrl;

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class);
        ResponseBean scanResponse = (ResponseBean) responseEntity.getBody();
        if (scanResponse.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
            Timestamp now = MyUtils.getCurrentSystemTimestamp();
            AssetNetWorkPo anwPo = assetNetworkMapper.getNetWorkinfo(assetUuid);
            if (anwPo == null) {
                anwPo = new AssetNetWorkPo();
                anwPo.setAsset_uuid(assetUuid);
                anwPo.setUuid(UUID.randomUUID().toString());
                anwPo.setCreate_time(now);
            }

            Map<String,String> payloadMap = (Map<String, String>) scanResponse.getPayload();
            anwPo.setUrl(tUrl);
            anwPo.setUrl_duration(payloadMap.get("total_time"));
            anwPo.setUrl_time(now);

            assetNetworkMapper.addNetWOrkData(anwPo);
            return scanResponse;
        }
        return scanResponse;
    }
}
