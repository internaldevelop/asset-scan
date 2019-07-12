package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.SystemConfigPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SystemConfigsMapper {
    /**
     * 新建一条系统配置记录
     */
    @Insert("INSERT INTO system_configs( " +
            "name, value) " +
            "VALUES ( " +
            "#{name}, #{value}) ")
    int addSystemConfig(SystemConfigPo systemConfigPo);

    /**
     * 获得所有的系统配置记录
     * @return
     */
    @Select("SELECT * FROM system_configs ")
    List<SystemConfigPo> getAllSystemConfigs();

    /**
     * 更新指系统配置信息
     * @param systemConfigPo
     * @return
     */
    @Update("UPDATE system_configs s SET " +
            "name=#{name}, value=#{value} " +
            "WHERE " +
            "s.name=#{name} ")
    int updateSystemConfig(SystemConfigPo  systemConfigPo);

    /**
     * 删除指定的一条系统配置
     * @param name
     * @return
     */
    @Delete("DELETE FROM system_configs WHERE name=#{name} ")
    int deleteSystemConfig(String name);

    /**
     * 根据name获取指定系统配置记录
     * @param name
     * @return
     */
    @Select("SELECT * FROM system_configs s WHERE s.name=#{name}")
    SystemConfigPo getSystemConfigByName(@Param("name") String name);
}
