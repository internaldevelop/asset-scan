package com.toolkit.assetscan.controller;

import com.toolkit.assetscan.bean.po.ProjectPo;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.service.ProjectManageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping(value = "/api/projects")
@Api(value = "06. 项目管理接口", tags = "06-Projects Manager API")
public class ProjectManageApi {
    private Logger logger = LoggerFactory.getLogger(ProjectManageApi.class);

    private final ProjectManageService projectManageService;
    private final ResponseHelper responseHelper;

    @Autowired
    public ProjectManageApi(ProjectManageService projectManageService, ResponseHelper responseHelper) {
        this.projectManageService = projectManageService;
        this.responseHelper = responseHelper;
    }

    /**
     * 6.1 添加一个新的项目
     * @param projectPo 项目参数
     * @param bindingResult 绑定数据的判定结果
     * @return payload: 项目名和项目的UUID
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    Object addTask(@ModelAttribute ProjectPo projectPo, BindingResult bindingResult) {
        return projectManageService.addProject(projectPo);
    }

    /**
     * 6.2 移除一个项目
     * @param projectUuid 项目的 UUID
     * @return payload: 无
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public @ResponseBody
    Object removeTask(@RequestParam("uuid") String projectUuid) {
        return projectManageService.removeProject(projectUuid);
    }

    /**
     * 6.3 获取所有的项目
     * @return payload: 所有项目的数组 （JSON 格式）
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllProjects() {
        return projectManageService.getAllProjects();
    }

    /**
     * 6.4 根据指定的 UUID 获取一条项目参数
     * @param projectUuid 项目的 UUID
     * @return payload: 项目参数（项目记录及外键字段的含义）
     */
    @RequestMapping(value = "/get-project", method = RequestMethod.GET)
    public @ResponseBody
    Object getProject(@RequestParam("uuid") String projectUuid) {
        return projectManageService.getProject(projectUuid);
    }

    /**
     * 6.5 更新一条项目
     * @param projectPo 项目参数
     * @return payload: 项目名和项目的 UUID
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody
    Object updateProject(@ModelAttribute ProjectPo projectPo) {
        return projectManageService.updateProject(projectPo);
    }

    /**
     * 6.7 获取所有项目资产信息
     * @return payload: 项目详细信息
     */
    @RequestMapping(value = "/all-project-details", method = RequestMethod.GET)
    public @ResponseBody
    Object getAllProjectDetailInfo() {
        return projectManageService.getAllProjectDetailInfo();
    }
}
