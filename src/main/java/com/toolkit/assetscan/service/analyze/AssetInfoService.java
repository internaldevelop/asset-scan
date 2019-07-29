package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class AssetInfoService {
    @Autowired
    RestTemplate restTemplate;

    public JSONObject getAssetInfo(String assetIP, String infoTypes) {
        // 构造URL
        String ip = "http://" + assetIP + ":8191";
        String url = ip + "/asset-info/acquire?types={types}";

        // 构造参数map
        HashMap<String, String> map = new HashMap<>();
        map.put("types", infoTypes);

        // 向节点发送请求，并返回节点的响应结果
        try {
            ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class, map);
            ResponseBean responseBean = (ResponseBean) responseEntity.getBody();
            if (responseBean.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
                JSONObject jsonMsg = (JSONObject) JSONObject.toJSON(responseBean.getPayload());
                return jsonMsg;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
