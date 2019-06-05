package com.toolkit.assetscan.controller;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.dao.mybatis.TaskExecuteResultsMapper;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import com.toolkit.assetscan.service.mq.TopicSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/utils")
public class UtilsManageApi {
    @Autowired
    private TaskExecuteResultsMapper taskExecuteResultsMapper;
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

    @Autowired
    private TopicSender msgProducer;

    /**
     * 发送RabbitMQ消息 - 生产者
     * @return
     */
    @RequestMapping(value = "/send-msg", method = RequestMethod.GET)
    public @ResponseBody
    Object sendMsg(@RequestParam("topic") String topic, @RequestParam("message") String message) {

        msgProducer.send(topic, message);
        return "MQ message sended";
    }

    /**
     * 测试设置 session 参数
     * @param attribute
     * @param value
     * @param request
     * @return
     */
    @RequestMapping(value = "set-session", method = RequestMethod.GET)
    public @ResponseBody
    Object setSessionUserName(@RequestParam("attribute") String attribute,
                              @RequestParam("value") String value,
                              HttpServletRequest request) {
        request.getSession().setAttribute(attribute, value);
        return request.getSession().getId();
    }

    @RequestMapping(value = "get-result-history", method = RequestMethod.POST)
    public @ResponseBody
    Object getResultHistory(@RequestParam("begin_time") java.sql.Timestamp beginTime,
                            @RequestParam("end_time") java.sql.Timestamp endTime,
                            @RequestParam("policy_uuid_list") String policyUuidList) {
        return taskExecuteResultsMapper.getHistoryRiskInfo(beginTime, endTime, policyUuidList, "");
    }


}
