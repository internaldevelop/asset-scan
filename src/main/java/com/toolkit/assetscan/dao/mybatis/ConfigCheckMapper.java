package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.ConfigCheckResultPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ConfigCheckMapper {
    @Insert("INSERT INTO config_check_results (\n" +
            "    uuid, base_line, asset_uuid, \n" +
            "\t\tscan_uuid, config_type, config_info, \n" +
            "\t\tcheck_item, creator_uuid, \n" +
            "\t\trisk_level, risk_desc, solution, \n" +
            "\t\tcreate_time)\n" +
            "VALUES (\n" +
            "    #{uuid}, #{base_line}, #{asset_uuid}, \n" +
            "\t\t#{scan_uuid}, #{config_type}, #{config_info}, \n" +
            "\t\t#{check_item}, #{creator_uuid}, \n" +
            "\t\t#{risk_level}, #{risk_desc}, #{solution}, \n" +
            "\t\t#{create_time, jdbcType=TIMESTAMP}\n" +
            "\t\t)")
    int addCheckResult(ConfigCheckResultPo resultPo);

    @Select("SELECT " +
            "    id, uuid, base_line, asset_uuid, \n" +
            "    scan_uuid, config_type, config_info, \n" +
            "    check_item, creator_uuid, \n" +
            "    risk_level, risk_desc, solution, \n" +
            "    create_time\n" +
            "FROM config_check_results cs \n" +
            "WHERE cs.scan_uuid=#{scan_uuid}")
    List<ConfigCheckResultPo> getScanResults(@Param("scan_uuid") String scanUuid);

    @Select("SELECT " +
            "    id, uuid, base_line, asset_uuid, \n" +
            "    scan_uuid, config_type, config_info, \n" +
            "    check_item, creator_uuid, \n" +
            "    risk_level, risk_desc, solution, \n" +
            "    create_time\n" +
            "FROM config_check_results cs \n" +
            "WHERE cs.scan_uuid=#{scan_uuid} AND cs.config_type=#{config_type}")
    List<ConfigCheckResultPo> getScanTypeResults(@Param("scan_uuid") String scanUuid, @Param("config_type") String configType);

    @Select("SELECT " +
            "    id, uuid, base_line, asset_uuid, \n" +
            "    scan_uuid, config_type, config_info, \n" +
            "    check_item, creator_uuid, \n" +
            "    risk_level, risk_desc, solution, \n" +
            "    create_time\n" +
            "FROM config_check_results cs \n" +
            "WHERE cs.uuid=#{uuid}")
    ConfigCheckResultPo getResult(@Param("uuid") String resultUuid);
}
