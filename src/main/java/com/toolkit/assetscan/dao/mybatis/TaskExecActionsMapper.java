package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.ExecActionsCountInfoDto;
import com.toolkit.assetscan.bean.dto.ExecActionsInfoDto;
import com.toolkit.assetscan.bean.po.TaskExecuteActionPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskExecActionsMapper {
    /**
     * 读取所有有效的任务执行记录信息
     * @return 所有有效的任务执行记录信息的集合
     */
    @Select("SELECT * FROM exec_actions ex WHERE ex.status>=0 ")
    List<TaskExecuteActionPo> allTaskExecActions();

    /**
     * 添加一条任务执行记录信息
     * @param executeActionPo 除了id ，其它字段都包含
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Insert("INSERT INTO exec_actions( " +
            "uuid, task_uuid, project_uuid, " +
            "user_uuid, comment, status, " +
            "exec_time) " +
            "VALUES ( " +
            "#{uuid}, #{task_uuid}, #{project_uuid}, " +
            "#{user_uuid}, #{comment}, #{status}, " +
            "#{exec_time, jdbcType=TIMESTAMP}) "
    )
    int addTaskExecAction(TaskExecuteActionPo executeActionPo);

    /**
     * 读取所有的执行记录信息（含项目、任务、操作员、资产、时间等）
     * @return
     */
    @Select("SELECT \n" +
            "  ea.uuid AS action_uuid, \n" +
            "  ea.exec_time, \n" +
            "  ea.task_uuid, \n" +
            "  ta.name AS task_name, \n" +
            "  ea.project_uuid, \n" +
            "  pj.name AS project_name, \n" +
            "  ea.user_uuid AS operator_uuid, \n" +
            "  u.account AS operator_account, \n" +
            "  u.name AS operator_name, \n" +
            "  a.uuid AS asset_uuid, \n" +
            "  a.name AS asset_name \n" +
            "FROM exec_actions ea \n" +
            "INNER JOIN users u ON ea.user_uuid = u.uuid \n" +
            "INNER JOIN tasks ta ON ea.task_uuid = ta.uuid \n" +
            "INNER JOIN projects pj ON ea.project_uuid = pj.uuid \n" +
            "INNER JOIN assets a ON ta.asset_uuid = a.uuid\n" +
            "WHERE ea.status>=0 \n")
    List<ExecActionsInfoDto> queryAllExecActionInfos();

    @Select("SELECT \n" +
            "   t.uuid AS task_uuid,\n" +
            "   t.`name` AS task_name,\n" +
            "   count(1) AS exec_count\n" +
            "FROM exec_actions ex\n" +
            "INNER JOIN tasks t ON ex.task_uuid = t.uuid\n" +
            "GROUP BY \n" +
            "   t.`uuid`, t.`name`")
    List<ExecActionsCountInfoDto> countExecByTaskName();

    @Select("SELECT \n" +
            "    p.uuid AS project_uuid,\n" +
            "    p.`name` AS project_name,\n" +
            "    count(1) AS exec_count\n" +
            "FROM exec_actions ex\n" +
            "INNER JOIN projects p ON ex.project_uuid = p.uuid\n" +
            "GROUP BY \n" +
            "    p.`uuid`, p.`name`")
    List<ExecActionsCountInfoDto> countExecByProjectName();

    @Select("SELECT \n" +
            "    u.uuid AS operator_uuid,\n" +
            "    u.`name` AS operator_name,\n" +
            "    count(1) AS exec_count\n" +
            "FROM exec_actions ex\n" +
            "INNER JOIN users u ON ex.user_uuid = u.uuid\n" +
            "GROUP BY \n" +
            "    u.`uuid`, u.`name`")
    List<ExecActionsCountInfoDto> countExecByOperatorName();

    @Select("SELECT \n" +
            "    a.uuid AS asset_uuid,\n" +
            "    a.`name` AS asset_name,\n" +
            "    count(1) AS exec_count\n" +
            "FROM exec_actions ex\n" +
            "INNER JOIN tasks t ON ex.task_uuid = t.uuid\n" +
            "INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            "GROUP BY \n" +
            "    a.`uuid`, a.`name`")
    List<ExecActionsCountInfoDto> countExecByAssetName();
}
