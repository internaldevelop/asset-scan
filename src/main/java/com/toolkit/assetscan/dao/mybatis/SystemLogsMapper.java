package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.SystemLogsDto;
import com.toolkit.assetscan.bean.po.SystemLogPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SystemLogsMapper {
    @Insert("INSERT INTO system_logs( \n" +
            "uuid, type, title, \n" +
            "contents, create_user_uuid, \n" +
            "create_time) \n" +
            "VALUES ( \n" +
            "#{uuid}, #{type}, #{title}, \n" +
            "#{contents}, #{create_user_uuid}, \n" +
            "#{create_time, jdbcType=TIMESTAMP}) ")
    int addLog(SystemLogPo systemLogPo);

    @Select("SELECT \n" +
            "    s.id, \n" +
            "    s.uuid, \n" +
            "    s.type, \n" +
            "    s.title, \n" +
            "    s.contents, \n" +
            "    s.create_time, \n" +
            "    s.create_user_uuid, \n" +
            "    u.account AS create_user_account, \n" +
            "    u.name AS create_user_name \n" +
            "FROM system_logs s \n" +
            "LEFT JOIN users u ON u.uuid=s.create_user_uuid")
    List<SystemLogsDto> getAllLogs();
}
