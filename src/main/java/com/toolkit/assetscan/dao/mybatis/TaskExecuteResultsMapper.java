package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.ExecRiskInfoDto;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.dto.TaskResultsStatisticsDto;
import com.toolkit.assetscan.bean.po.TaskExecuteResultsPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskExecuteResultsMapper {
    /**
     * 新增一条测试结果记录，只包含任务策略启动的信息，不包含测试或分析结果
     * @param resultPo
     * @return
     */
    @Insert("INSERT INTO task_execute_results( \n" +
            "uuid, task_uuid, start_time, \n" +
            "process_flag, policy_uuid, exec_action_uuid, \n" +
            "create_time) \n" +
            "VALUES ( \n" +
            "#{uuid}, #{task_uuid}, #{start_time, jdbcType=TIMESTAMP}, \n" +
            "#{process_flag}, #{policy_uuid}, #{exec_action_uuid}, \n" +
            "#{create_time, jdbcType=TIMESTAMP}) ")
    int addExecuteRecord(TaskExecuteResultsPo resultPo);

    /**
     * 更新测试或分析结果
     * @param resultPo
     * @return
     */
    @Update("UPDATE task_execute_results t SET \n" +
            "end_time=#{end_time, jdbcType=TIMESTAMP}, \n" +
            "results=#{results}, process_flag=#{process_flag}, \n" +
            "risk_level=#{risk_level}, risk_desc=#{risk_desc}, solutions=#{solutions} \n" +
            "WHERE \n" +
            "t.uuid=#{uuid} "
    )
    int updateExecResult(TaskExecuteResultsPo resultPo);

    /**
     * 读取任务结果
     * @return 成功时返回 TaskExecuteResultsProps 的列表，失败时返回 null1
     */
    @Select("SELECT\n" +
//            "   ter.id,\n" +
            "	ter.uuid,\n" +
            "	ter.task_uuid,\n" +
            "	ter.start_time,\n" +
            "	ter.end_time,\n" +
//            "   ter.results,\n" +
//            "   ter.process_flag,\n" +
            "	ter.risk_level,\n" +
            "   ter.risk_desc,\n" +
            "	ter.solutions,\n" +
//            "	ter.policy_uuid,\n" +
//            "	ter.create_time,\n" +
//            "	ter.exec_action_uuid,\n",

            "	t. NAME AS task_name,\n" +
            "	t.description AS description,\n" +
            "	t.id AS task_id,\n" +
            "	a. NAME AS assets_name,\n" +
            "	a.ip AS assets_ip,\n" +
            "   p.name AS policy_name\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            " where t.`name` LIKE '%${taskNameIpType}%' OR a.ip LIKE '%${taskNameIpType}%' OR p.name LIKE '%${taskNameIpType}%'\n" +
            " ORDER BY ter.id DESC")
    List<TaskResultsDto> allTaskResults(@Param("taskNameIpType") String taskNameIpType);

    @Select("SELECT\n" +
            "	p.`name` AS policy_name,\n" +
            "	a.os_type AS os_type,\n" +
            "	COUNT(1) AS num\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            " GROUP BY\n" +
            "	p.id,\n" +
            "	a.os_type")
    List<TaskResultsStatisticsDto> getResultsStatistics();

    @Select("SELECT\n" +
            "	g.`name` AS policy_group_name,\n" +
            "	a.os_type AS os_type,\n" +
            "	COUNT( 1 ) AS num \n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            "	INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            "	INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid \n" +
            "	INNER JOIN policy_groups g ON g.uuid = p.group_uuid\n" +
            "GROUP BY g.id, a.os_type")
    List<TaskResultsStatisticsDto> getResultsStatisticsGroup();

    @Select("SELECT\n" +
            "	p.`name` AS policy_name,\n" +
            "	COUNT(1) AS num\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            " GROUP BY\n" +
            "	p.id")
    List<TaskResultsStatisticsDto> getResultsPolicieStatistics();

    @Select("SELECT\n" +
            "	g.`name` AS policy_group_name,\n" +
            "	COUNT( 1 ) AS num \n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            "	INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid \n" +
            "	INNER JOIN policy_groups g ON g.uuid = p.group_uuid\n" +
            " GROUP BY g.id")
    List<TaskResultsStatisticsDto> getResultsPolicieStatisticsGroup();

    @Select("SELECT\n" +
            "	a.os_type AS os_type,\n" +
            "	COUNT(1) AS num\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            " GROUP BY\n" +
            "	a.os_type")
    List<TaskResultsStatisticsDto> getResultsSysStatistics();

    @Select("SELECT\n" +
            "	t.* \n" +
            " FROM\n" +
            "	task_execute_results t\n" +
            " WHERE t.exec_action_uuid=#{exec_action_uuid}")
    List<TaskExecuteResultsPo> getTaskExecResultsByExecUuid(@Param("exec_action_uuid") String execUuid);

    @Select("SELECT\n" +
            "	t.id, \n" +
            "	t.uuid, \n" +
            "	t.start_time, \n" +
            "	t.end_time, \n" +
            "	t.process_flag, \n" +
            "	t.risk_level, \n" +
            "	t.risk_desc, \n" +
            "	t.solutions, \n" +
            "	t.policy_uuid \n" +
            " FROM\n" +
            "	task_execute_results t\n" +
            " WHERE t.exec_action_uuid=#{exec_action_uuid}")
    List<TaskExecuteResultsPo> getTaskExecBriefByExecUuid(@Param("exec_action_uuid") String execUuid);

    @Select("SELECT\n" +
            "	t.uuid AS result_uuid, \n" +
            "	t.start_time, \n" +
            "	t.end_time, \n" +
            "	t.end_time-t.start_time AS run_time, \n" +
            "	a.name AS asset_name, \n" +
            "	t.process_flag, \n" +
            "	t.risk_level, \n" +
            "	t.risk_desc, \n" +
            "	t.solutions, \n" +
            "	t.policy_uuid, \n" +
            "	p.name AS policy_name, \n" +
            "	p.group_uuid AS policy_group_uuid, \n" +
            "	pg.name AS policy_group_name \n" +
            " FROM\n" +
            "	task_execute_results t\n" +
            " INNER JOIN exec_actions ea ON ea.uuid = t.exec_action_uuid\n" +
            " INNER JOIN tasks ta ON ea.task_uuid = ta.uuid\n" +
            " INNER JOIN assets a ON ta.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON t.policy_uuid = p.uuid\n" +
            " INNER JOIN policy_groups pg ON p.group_uuid = pg.uuid\n" +
            " WHERE t.exec_action_uuid=#{exec_action_uuid} AND t.risk_level=#{risk_level} \n")
    List<ExecRiskInfoDto> getRiskInfo(@Param("exec_action_uuid") String execUuid,
                                      @Param("risk_level") int riskLevel);

    /** 举例
     * SELECT
     * 	r.uuid,
     * 	r.create_time,
     * 	r.policy_uuid
     * FROM task_execute_results r
     * WHERE
     * 	r.create_time>='2019-05-31 16:14:49' AND r.create_time<='2019-06-04 10:11:45'
     * 	AND IF(LENGTH('123')=0, 1, FIND_IN_SET(r.policy_uuid, '87b907eb-234a-4409-825e-7d04acfaae40,e0db6657-9c89-4afc-9f55-5516030788ee,'));
     * 	AND r.risk_desc LIKE '%审计%'
     * @param beginTime
     * @param endTime
     * @param policyUuidList
     * @param scanResult
     * @return
     */
    @Select("SELECT\n" +
            "	t.uuid AS result_uuid, \n" +
            "	t.start_time, \n" +
            "	t.end_time, \n" +
            "	t.end_time-t.start_time AS run_time, \n" +
            "	a.name AS asset_name, \n" +
            "	t.process_flag, \n" +
            "	t.risk_level, \n" +
            "	t.risk_desc, \n" +
            "	t.solutions, \n" +
            "	u.uuid AS user_uuid, \n" +
            "	u.account AS user_account, \n" +
            "	u.name AS user_name, \n" +
            "	t.policy_uuid, \n" +
            "	p.name AS policy_name, \n" +
            "	p.group_uuid AS policy_group_uuid, \n" +
            "	pg.name AS policy_group_name \n" +
            " FROM\n" +
            "	task_execute_results t\n" +
            " INNER JOIN exec_actions ea ON ea.uuid = t.exec_action_uuid\n" +
            " INNER JOIN users u ON ea.user_uuid = u.uuid\n" +
            " INNER JOIN tasks ta ON ea.task_uuid = ta.uuid\n" +
            " INNER JOIN assets a ON ta.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON t.policy_uuid = p.uuid\n" +
            " INNER JOIN policy_groups pg ON p.group_uuid = pg.uuid\n" +
            " WHERE\n" +
            "   t.create_time>=#{begin_time, jdbcType=TIMESTAMP} AND t.create_time<=#{end_time, jdbcType=TIMESTAMP} \n" +
            "   AND IF(LENGTH(#{policy_uuid_list})=0, 1, FIND_IN_SET(t.policy_uuid, #{policy_uuid_list})) \n" +
            "   AND t.risk_desc LIKE '%${scan_result}%' \n"
    )
    List<ExecRiskInfoDto> getHistoryRiskInfo(@Param("begin_time") java.sql.Timestamp beginTime,
                                             @Param("end_time") java.sql.Timestamp endTime,
                                             @Param("policy_uuid_list") String policyUuidList,
                                             @Param("scan_result") String scanResult);
}
