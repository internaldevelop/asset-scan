package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.TaskInfosDto;
import com.toolkit.assetscan.bean.po.TaskPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TasksMapper {
    /**
     * 读取所有有效的任务信息
     * @return 所有有效任务信息的集合
     */
    @Select("SELECT * FROM tasks t WHERE t.status>=0 ")
    List<TaskPo> allTasks();

    /**
     * 添加一条任务信息
     * @param taskPo 除了id ，其它字段都包含
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Insert("INSERT INTO tasks( " +
            "uuid, name, code, " +
            "description, asset_uuid, policies_name, " +
            "create_user_uuid, status, " +
            "update_time, " +
            "create_time) " +
            "VALUES ( " +
            "#{uuid}, #{name}, #{code}, " +
            "#{description}, #{asset_uuid}, #{policies_name}, " +
            "#{create_user_uuid}, #{status}, " +
            "#{update_time, jdbcType=TIMESTAMP}, " +
            "#{create_time, jdbcType=TIMESTAMP}) "
    )
    int addTask(TaskPo taskPo);

    /**
     * 更新任务信息
     * @param taskPo id / uuid / status / create_time 不可更改
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Update("UPDATE tasks t SET " +
            "name=#{name}, code=#{code}, " +
            "description=#{description}, asset_uuid=#{asset_uuid}, policies_name=#{policies_name}, " +
            "update_time=#{update_time}," +
            "create_user_uuid=#{create_user_uuid} " +
            "WHERE " +
            "uuid=#{uuid} AND t.status>=0  "
    )
    int updateTask(TaskPo taskPo);

    /**
     * 根据指定的UUID删除一条任务记录
     * @param taskUuid 任务UUID
     * @return 影响的记录数， >0 表示成功
     */
    @Delete("DELETE FROM tasks WHERE uuid=#{uuid}")
    int deleteTask(@Param("uuid") String taskUuid);

    /**
     * 根据指定的UUID获取一条任务记录
     * @param taskUuid 任务UUID
     * @return
     */
    @Select("SELECT * FROM tasks t WHERE t.uuid=#{uuid} AND t.status>=0 ")
    TaskPo getTaskByUuid(@Param("uuid") String taskUuid);


    /**
     * 任务结果
     * @return 成功时返回 TaskExecuteResultsProps 的列表，失败时返回 null1
     */
    @Select("SELECT\n" +
            "	t.uuid,\n" +
            "	t.id AS task_id,\n" +
            "	t.name AS task_name,\n" +
            "	t.status,\n" +
            "	t.update_time,\n" +
            "	a.uuid AS asset_uuid,\n" +
            "	a.name AS assets_name,\n" +
            "	a.ip AS assets_ip,\n" +
            "	a.port AS assets_port,\n" +
            "	a.os_type,\n" +
            "	a.os_ver\n" +
            " FROM\n" +
            "	tasks t\n" +
            " INNER JOIN assets a ON t.asset_uuid = a.uuid")
    List<TaskInfosDto> getAllTaskInfos();
}