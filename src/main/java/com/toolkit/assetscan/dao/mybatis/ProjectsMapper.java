package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.ProjectDetailInfoDto;
import com.toolkit.assetscan.bean.po.ProjectPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectsMapper {
    /**
     * 读取所有有效的项目信息
     * @return 所有有效项目信息的集合
     */
    @Select("SELECT \n" +
            "    * \n" +
            "FROM projects p \n" +
            "WHERE \n" +
            "    p.status>=0 \n" +
            "    AND p.uuid<>#{default_project}")
    List<ProjectPo> allProjects(@Param("default_project") String defaultProject);

    /**
     * 添加一条项目信息
     * @param projectPo 除了id ，其它字段都包含
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Insert("INSERT INTO projects( " +
            "uuid, name, code, " +
            "tasks, run_time_mode, output_mode, task_number, " +
            "create_user_uuid, status, process_flag, " +
            "create_time) " +
            "VALUES ( " +
            "#{uuid}, #{name}, #{code}, " +
            "#{tasks}, #{run_time_mode}, #{output_mode}, #{task_number}," +
            "#{create_user_uuid}, #{status}, #{process_flag}," +
            "#{create_time, jdbcType=TIMESTAMP}) "
    )
    int addProject(ProjectPo projectPo);

    /**
     * 更新项目信息
     * @param projectPo id / uuid / status / create_time 不可更改
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Update("UPDATE projects p SET " +
            "name=#{name}, code=#{code}, " +
            "tasks=#{tasks}, run_time_mode=#{run_time_mode}, output_mode=#{output_mode}, " +
            "process_flag=#{process_flag}," +
            "task_number=#{task_number} " +
            "WHERE " +
            "uuid=#{uuid} AND p.status>=0  "
    )
    int updateProject(ProjectPo projectPo);

    /**websocket
     * 根据指定的UUID删除一条项目记录
     * @param projectUuid 项目UUID
     * @return 影响的记录数， >0 表示成功
     */
    @Delete("DELETE FROM projects WHERE uuid=#{uuid}")
    int deleteProject(@Param("uuid") String projectUuid);

    /**
     * 根据指定的UUID获取一条项目记录
     * @param projectUuid 项目UUID
     * @return 返回记录
     */
    @Select("SELECT * FROM projects p WHERE p.uuid=#{uuid} AND p.status>=0 ")
    ProjectPo getProjectByUuid(@Param("uuid") String projectUuid);
}
