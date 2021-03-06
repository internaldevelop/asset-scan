package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.Helper.SystemLogsHelper;
import com.toolkit.assetscan.bean.po.PolicyPo;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.service.PolicyGroupService;
import com.toolkit.assetscan.service.PolicyManageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/policies")
@Api(value = "04. 策略管理接口", tags = "04-Policies Manager API")
public class PolicyManageApi {
    private Logger logger = LoggerFactory.getLogger(PolicyManageApi.class);
    private final PolicyManageService policyManageService;
    private final PolicyGroupService policyGroupService;
    @Autowired
    private SystemLogsHelper systemLogs;

    public PolicyManageApi(PolicyManageService policyManageService, PolicyGroupService policyGroupService) {
        this.policyManageService = policyManageService;
        this.policyGroupService = policyGroupService;
    }

    /**
     * 4.1 添加一个新的策略
     * @param policy policy 参数
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 策略名和策略的 UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addPolicy(@ModelAttribute PolicyPo policy, BindingResult bindingResult) {
        ResponseBean response = policyManageService.addPolicy(policy);
        // 系统日志
        systemLogs.logEvent(response, "新增策略", "添加新策略");

        return response;
    }

    /**
     * 4.2 移除一个策略
     * @param policyUuid 策略的 UUID
     * @return payload: 无
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    Object removePolicy(@RequestParam("uuid") String policyUuid) {
        ResponseBean response = policyManageService.removePolicy(policyUuid);
        // 系统日志
        systemLogs.logEvent(response, "删除策略", "删除策略（ID：" + policyUuid + "）");
        return response;
    }

    /**
     * 4.3 获取所有的策略
     * @return payload: 所有策略的数组 （JSON 格式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllPolicies() {
        return policyManageService.getAllPolicies(false);
    }

    /**
     * 4.4 根据指定的 UUID 获取一条策略参数
     * @param policyUuid 策略的 UUID
     * @return payload: 策略参数（策略记录）
     */
    @RequestMapping(value = "/get-policy", method = RequestMethod.GET)
    public @ResponseBody
    Object getPolicy(@RequestParam("uuid") String policyUuid) {
        return policyManageService.getPolicy(policyUuid);
    }

    /**
     * 4.5 更新一条策略记录
     * @param policy 策略参数
     * @return payload: 策略名和策略的 UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object  updatePolicy(@ModelAttribute PolicyPo policy) {
        ResponseBean response = policyManageService.updatePolicy(policy);
        // 系统日志
        systemLogs.logEvent(response, "更新策略", "更新策略（ID：" + policy.getUuid() + "）");
        return response;
    }

    /**
     * 4.6 根据groupIdd获取所在组所有的策略
     * @param policyGroupId
     * @return
     */
    @RequestMapping(value = "/get-policies-by-group-id", method = RequestMethod.GET)
    public @ResponseBody
    Object getPoliciesByGroupId(@RequestParam("groupId") String policyGroupId) {
        String policyGroupUuid = policyGroupService.getUuidByGroupId(Integer.parseInt(policyGroupId));
        return policyManageService.getPoliciesByGroupUuid(policyGroupUuid);
    }

    /**
     * 4.7 根据groupCoded获取所在组所有的策略
     * @param policyGroupCode
     * @return
     */
    @RequestMapping(value = "/get-policies-by-group-code", method = RequestMethod.GET)
    public Object getPoliciesByGroupCode(@RequestParam("groupCode") String policyGroupCode) {
        return policyManageService.getPoliciesByGroupCode(policyGroupCode);
    }

    /**
     * 4.8 code:1 补丁安装情况; code:2 系统服务分析; code:3 系统文件安全防护分析; code:4 用户账号配置分析; code:5 口令策略配置分析; code:6 网络通信配置分析; code:7 日志审计分析;
     * @param code
     * @return
     */
    @RequestMapping(value = "/statistics-report", method = RequestMethod.GET)
    public @ResponseBody
    Object statisticsReport(@RequestParam("code") String code) {
        return policyManageService.statisticsReport(code);
    }

    /**
     * 4.9 统计根据groupIdd获取所在组所有的策略
     * @param policyGroupId
     * @return
     */
    @RequestMapping(value = "/statistics-policies-by-group", method = RequestMethod.GET)
    public @ResponseBody
    Object statisticsPoliciesByGroup(@RequestParam("groupId") String policyGroupId) {
        return policyManageService.statisticsPoliciesByGroup(policyGroupId);
    }

    /**
     * 4.10 联合资产和策略组表查询策略详细信息
     * @return
     */
    @RequestMapping(value = "/all-detail-info", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllPolicyDetailInfos() {
        return policyManageService.getAllPolicyDetailInfos();
    }

    /**
     * 4.11 根据groupUuid获取所在组所有的策略
     * @param policyGroupUuid
     * @return
     */
    @RequestMapping(value = "/get-policies-by-group-uuid", method = RequestMethod.GET)
    public @ResponseBody
    Object getPoliciesByGroupUuid(@RequestParam("groupUuid") String policyGroupUuid) {
        return policyManageService.getPoliciesByGroupUuid(policyGroupUuid);
    }

    /**
     * 4.12 获取所有策略的简要信息
     * @return
     */
    @RequestMapping(value = "/all-brief", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllPoliciesBrief() {
        return policyManageService.getAllPolicies(true);
    }

    /**
     * 4.13 策略名称是否唯一
     * @param policyName
     * @param policyUuid  没有提供此参数，或参数为空，表示全局检查名称唯一性；否则检查除自己外，
     *                  其他策略是否使用该名称
     * @return
     */
    @RequestMapping(value = "/check-unique-name", method = RequestMethod.GET)
    @ResponseBody
    public Object isPolicyNameExist(@RequestParam("policy_name") String policyName,
                                   @RequestParam(value = "policy_uuid", required = false) String policyUuid) {
        return policyManageService.checkPolicyNameExist(policyName, policyUuid);
    }

}
