package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.po.PolicyGroupPo;
import com.toolkit.assetscan.service.PolicyGroupService;
import io.swagger.annotations.Api;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/policy-groups")
@Api(value = "51. 策略分组字典表接口", tags = "51-Policy Group API")
public class PolicyGroupApi {
    private final PolicyGroupService policyGroupService;

    public PolicyGroupApi(PolicyGroupService policyGroupService) {
        this.policyGroupService = policyGroupService;
    }

    /**
     * 51.1 获取所有分组的数据
     * @return payload: 所有分组数据的数组
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllGroups() {
        return policyGroupService.getAllGroups();
    }

    /**
     * 51.2 分组的字典表中新增一条记录
     * @param groupProps 分组
     * @return payload: 分组名和分组的 UUID
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody
    Object addGroup(@ModelAttribute PolicyGroupPo groupProps, BindingResult bindingResult) {
        return policyGroupService.addGroup(groupProps);
    }

    /**
     * 根据id获取策略组
     * @param groupId
     * @return
     */
    @RequestMapping(value = "/get-group", method = RequestMethod.GET)
    public @ResponseBody
    Object getPolicyGroupById(@RequestParam("id") int groupId) {
        return policyGroupService.getPolicyGroup(groupId);
    }

}
