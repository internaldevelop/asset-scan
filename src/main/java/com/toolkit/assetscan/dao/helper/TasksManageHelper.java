package com.toolkit.assetscan.dao.helper;

import com.toolkit.assetscan.bean.po.TaskPo;
import com.toolkit.assetscan.dao.mybatis.TasksMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TasksManageHelper {
    private final TasksMapper tasksMapper;

    @Autowired
    public TasksManageHelper(TasksMapper tasksMapper) {
        this.tasksMapper = tasksMapper;
    }

    public boolean addTask(TaskPo taskPo) {
        int rv = tasksMapper.addTask(taskPo);
        return (rv > 0);
    }

    public boolean deleteTask(String taskUuid) {
        int rv = tasksMapper.deleteTask(taskUuid);
        return (rv > 0);
    }

    public boolean updateTask(TaskPo taskPo) {
        int rv = tasksMapper.updateTask(taskPo);
        return (rv > 0);
    }
}
