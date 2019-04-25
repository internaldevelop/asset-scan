package com.toolkit.assetscan.controller;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/utils")
public class UtilsManageApi {
    private final ResponseHelper responseHelper;
    @Autowired
    public UtilsManageApi(ResponseHelper responseHelper) {
        this.responseHelper = responseHelper;
    }

    @RequestMapping(value = "/uuid", method = RequestMethod.GET)
    @ResponseBody
    public String getUuid() {
        return MyUtils.generateUuid();
    }

    @RequestMapping(value = "/json-pretty-format", method = RequestMethod.POST)
    @ResponseBody
    public Object getJsonPrettyFormat(@RequestParam("input") String input) {
        JSONObject jsonObject = JSONObject.parseObject(input);
        return jsonObject;
    }

    @RequestMapping(value = "/xml2json", method = RequestMethod.POST)
    @ResponseBody
    public Object xml2JSON(@RequestParam("input") String input) {
        return org.json.XML.toJSONObject(input);
    }
}
