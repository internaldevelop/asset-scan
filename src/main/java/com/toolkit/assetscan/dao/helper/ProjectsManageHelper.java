package com.toolkit.assetscan.dao.helper;

import com.toolkit.assetscan.bean.po.ProjectPo;
import com.toolkit.assetscan.dao.mybatis.ProjectsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectsManageHelper {
    private final ProjectsMapper projectsMapper;

    @Autowired
    public ProjectsManageHelper(ProjectsMapper projectsMapper) {
        this.projectsMapper = projectsMapper;
    }

    public boolean addProject(ProjectPo projectPo) {
        int rv = projectsMapper.addProject(projectPo);
        return (rv > 0);
    }

    public boolean deleteProject(String projectUuid) {
        int rv = projectsMapper.deleteProject(projectUuid);
        return (rv > 0);
    }

    public boolean updateProject(ProjectPo projectPo) {
        int rv = projectsMapper.updateProject(projectPo);
        return (rv > 0);
    }
}
