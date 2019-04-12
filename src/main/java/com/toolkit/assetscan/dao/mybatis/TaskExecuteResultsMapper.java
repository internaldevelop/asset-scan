package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.TaskExecuteResultsProps;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskExecuteResultsMapper {
    /**
     * 读取任务结果
     * @return 成功时返回 TaskExecuteResultsProps 的列表，失败时返回 null1
     */
    @Select("SELECT * FROM task_execute_results ter ")
    List<TaskExecuteResultsProps> allTaskResults();


}
