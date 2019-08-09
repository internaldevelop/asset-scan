package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.dto.TaskRunStatusDto;
import com.toolkit.assetscan.bean.po.AssetPo;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.dao.helper.TasksManageHelper;
import com.toolkit.assetscan.dao.mybatis.AssetsMapper;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.TaskRunStatusEnum;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

@Component
public class NetConnectService {
    private final AssetsMapper mAssetsMapper;
    private final ResponseHelper mResponseHelper;
    private final TasksMapper mtasksMapper;
    private final TasksManageHelper mtasksManageHelper;

    @Autowired
    AssetCollectScheduler assetCollectScheduler;
    @Autowired
    RestTemplate restTemplate;

    public NetConnectService(TasksMapper tasksMapper, TasksManageHelper tasksManageHelper, AssetsMapper assetsMapper, ResponseHelper responseHelper) {
        mtasksMapper = tasksMapper;
        mtasksManageHelper = tasksManageHelper;
        mAssetsMapper = assetsMapper;
        mResponseHelper = responseHelper;
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

        // 构造URL
        String strip = "http://" + assetPo.getIp() + ":8191";
        String url = strip + "/netconnect/param?ip="+ip;

        // 向节点发送请求，并返回节点的响应结果
        ResponseEntity<ResponseBean> responseEntity = restTemplate.getForEntity(url, ResponseBean.class);

        JSONObject jsonData = new JSONObject();

        JSONObject jsonMsg = (JSONObject)JSONObject.toJSON(responseEntity.getBody());

        jsonData.put("isconnect", jsonMsg.getJSONObject("payload").getString("isconnect"));
        return mResponseHelper.success(jsonData);
    }


    }
