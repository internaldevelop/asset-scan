package com.toolkit.assetscan.service;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.bean.dto.ProjectDetailInfoDto;
import com.toolkit.assetscan.bean.po.ProjectPo;
import com.toolkit.assetscan.dao.helper.ProjectsManageHelper;
import com.toolkit.assetscan.dao.mybatis.ProjectsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.enumeration.TaskStatusEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectManageService {
    private ResponseBean responseBean;
    private final ResponseHelper responseHelper;
    private final ProjectsMapper projectsMapper;
    private final ProjectsManageHelper projectsManageHelper;

    public ProjectManageService(ResponseHelper responseHelper, ProjectsMapper projectsMapper, ProjectsManageHelper projectsManageHelper) {
        this.responseHelper = responseHelper;
        this.projectsMapper = projectsMapper;
        this.projectsManageHelper = projectsManageHelper;
    }
    private boolean iCheckParams(ProjectPo projectPo) {
        responseBean = responseHelper.success();
        return true;
    }

    private ResponseBean successReturnInfo(String policyName, String code, String policyUuid) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("name", policyName);
        jsonData.put("code", code);
        jsonData.put("uuid", policyUuid);
        return responseHelper.success(jsonData);
    }

    public ResponseBean addProject(ProjectPo projectPo) {
        // 检查参数
        if (!iCheckParams(projectPo))
            return responseBean;

        // 为新任务随机分配一个UUID
        projectPo.setUuid(MyUtils.generateUuid());

        // 新项目状态设置为已激活
        projectPo.setStatus(TaskStatusEnum.TASK_ACTIVE.getStatus());

        // 记录新项目的创建时间
        java.sql.Timestamp currentTime = MyUtils.getCurrentSystemTimestamp();
        projectPo.setCreate_time(currentTime);

        // 往数据库里写入这条新策略
        if (!projectsManageHelper.addProject(projectPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);

        return successReturnInfo( projectPo.getName(), projectPo.getCode(), projectPo.getUuid() );
    }

    public ResponseBean removeProject(String projectUuid) {
        // 获取策略数据，找不到则返回错误
        ProjectPo projectPo = projectsMapper.getProjectByUuid(projectUuid);
        if (projectPo == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_PROJECT_NOT_FOUND);
        }

        // 移除该策略
        if (!projectsManageHelper.deleteProject(projectUuid)) {
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        }

        // 返回数据包含已删除策略的名称、代码和 UUID
        return successReturnInfo( projectPo.getName(), projectPo.getCode(), projectPo.getUuid() );
    }

    public ResponseBean getAllProjects() {
        List<ProjectPo> projectsList = projectsMapper.allProjects();
        return responseHelper.success(projectsList);
    }

    public ResponseBean getProject(String projectUuid) {
        ProjectPo project = projectsMapper.getProjectByUuid(projectUuid);
        if (project == null) {
            return responseHelper.error(ErrorCodeEnum.ERROR_PROJECT_NOT_FOUND);
        }

        return responseHelper.success(project);
    }

    public ResponseBean updateProject(ProjectPo projectPo) {
        if (!projectsManageHelper.updateProject(projectPo))
            return responseHelper.error(ErrorCodeEnum.ERROR_INTERNAL_ERROR);
        return successReturnInfo( projectPo.getName(), projectPo.getCode(), projectPo.getUuid() );
    }
}
