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
    @Select("SELECT * FROM projects p WHERE p.status>=0 ")
    List<ProjectPo> allProjects();

    /**
     * 添加一条项目信息
     * @param projectPo 除了id ，其它字段都包含
     * @return 影响记录数量，>0 表示成功，否则失败
     */
    @Insert("INSERT INTO projects( " +
            "uuid, name, code, " +
            "task_uuid, run_time_mode, output_mode, output_path, " +
            "create_user_uuid, status, " +
            "update_time, " +
            "create_time) " +
            "VALUES ( " +
            "#{uuid}, #{name}, #{code}, " +
            "#{task_uuid}, #{run_time_mode}, #{output_mode}, #{output_path}," +
            "#{create_user_uuid}, #{status}, " +
            "#{update_time, jdbcType=TIMESTAMP}, " +
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
            "task_uuid=#{task_uuid}, run_time_mode=#{run_time_mode}, output_mode=#{output_mode}, " +
            "update_time=#{update_time}," +
            "output_path=#{output_path} " +
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

    @Select("SELECT p.*,\n" +
            "   t.name AS task_name,\n" +
            "   FROM\n" +
            "	projects p\n" +
            "	INNER JOIN tasks t ON t.uuid = p.task_uuid\n")
    List<ProjectDetailInfoDto> getAllProjectDetailInfo();
}
