package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.PolicyGroupPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PolicyGroupsMapper {

    /**
     * 获得所有分组数据
     * @return PolicyGroupProps 的集合
     */
    @Select("SELECT * FROM policy_groups ")
    List<PolicyGroupPo> allGroups();

    /**
     * 新建一条分组记录
     * @param group 分组数据
     * @return >=1：成功；<=0：失败；
     */
    @Insert("INSERT INTO policy_groups( " +
            "uuid, name, code, " +
            "type, create_user_uuid, create_time, " +
            "status, baseline) " +
            "VALUES ( " +
            "#{uuid}, #{name}, #{code}, " +
            "#{type}, #{create_user_uuid}, #{create_time, jdbcType=TIMESTAMP}, " +
            "#{status}, #{baseline}) ")
    int addGroup(PolicyGroupPo group);

    @Select("SELECT * FROM policy_groups g WHERE g.id=#{id} ")
    PolicyGroupPo getGroupById(@Param("id") int groupId);

}
