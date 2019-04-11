package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.PolicyGroupProps;
import com.toolkit.assetscan.bean.PolicyProps;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PolicyGroupsMapper {

    /**
     * 获得所有分组数据
     * @return PolicyGroupProps 的集合
     */
    @Select("SELECT * FROM groups ")
    List<PolicyGroupProps> allGroups();

    /**
     * 新建一条分组记录
     * @param group 分组数据
     * @return >=1：成功；<=0：失败；
     */
    @Insert("INSERT INTO groups( " +
            "uuid, " +
            "name) " +
            "VALUES ( " +
            "#{uuid}, " +
            "#{name}) ")
    int addGroup(PolicyGroupProps group);

}
