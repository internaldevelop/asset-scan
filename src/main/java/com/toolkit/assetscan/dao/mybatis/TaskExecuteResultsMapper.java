package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.dto.TaskResultsStatisticsDto;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskExecuteResultsMapper {
    /**
     * 读取任务结果
     * @return 成功时返回 TaskExecuteResultsProps 的列表，失败时返回 null1
     */
    @Select("SELECT\n" +
            "	ter.uuid,\n" +
            "	ter.task_uuid,\n" +
            "	ter. CODE,\n" +
            "	ter.start_time,\n" +
            "	ter.end_time,\n" +
            "	ter.results,\n" +
            "	ter.process_flag,\n" +
            "	t. NAME AS task_name,\n" +
            "	t.description AS description,\n" +
            "	t.id AS task_id,\n" +
            "	a. NAME AS assets_name,\n" +
            "	a.ip AS assets_ip,\n" +
            "	p.solutions,\n" +
            "   p.risk_level,\n" +
            "   p.name AS policie_name\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON ter.policie_uuid = p.uuid")
    List<TaskResultsDto> allTaskResults();

    @Select("SELECT\n" +
            "	p.`name` AS policie_name,\n" +
            "	a.os_type AS os_type,\n" +
            "	COUNT(1) AS num\n" +
            " FROM\n" +
            "	task_execute_results ter\n" +
            " INNER JOIN tasks t ON ter.task_uuid = t.uuid\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid\n" +
            " INNER JOIN policies p ON ter.policie_uuid = p.uuid\n" +
            " GROUP BY\n" +
            "	p.id,\n" +
            "	a.os_type")
    List<TaskResultsStatisticsDto> getResultsStatistics();
}
