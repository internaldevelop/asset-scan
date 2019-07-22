package com.toolkit.assetscan.service.analyze;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;
import com.toolkit.assetscan.dao.mybatis.ConfigCheckMapper;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.utils.MyUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Data
public class AnalyzeSubject {
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    ConfigCheckMapper configCheckMapper;

    private int baseLine;
    private String assetUuid;
    private String scanUuid;
    private String configType;
    private String configInfo;
    private String checkItem;
    private int riskLevel;
    private String riskDesc;
    private String solution;

    public boolean saveCheckResult() {
        ConfigCheckResultPo resultPo = new ConfigCheckResultPo();
        resultPo.setUuid(MyUtils.generateUuid());
        resultPo.setBase_line(this.baseLine);
        resultPo.setAsset_uuid(this.assetUuid);
        resultPo.setScan_uuid(this.scanUuid);
        resultPo.setConfig_type(this.configType);
        resultPo.setConfig_info(this.configInfo);
        resultPo.setCheck_item(this.checkItem);
        resultPo.setRisk_level(this.riskLevel);
        resultPo.setRisk_desc(this.riskDesc);
        resultPo.setSolution(this.solution);
        resultPo.setCreator_uuid((String) httpServletRequest.getSession().getAttribute(Const.USER_UUID));
        resultPo.setCreate_time(MyUtils.getCurrentSystemTimestamp());
        return (configCheckMapper.addCheckResult(resultPo) == 1);
    }

    public String getCheckItemDesc(JSONObject items, BaseLineItemEnum itemEnum) {
        return items.getString(itemEnum.getName());
    }

    public boolean needCheck(JSONObject items, BaseLineItemEnum itemEnum) {
        return items.containsKey(itemEnum.getName());
    }

    public void saveCheckItem(JSONObject items, BaseLineItemEnum itemEnum) {
        this.checkItem = getCheckItemDesc(items, itemEnum);
    }
}
