package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.AssetScanRecordDto;
import com.toolkit.assetscan.bean.po.AssetScanDataPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AssetScanDataMapper {
    @Insert("INSERT INTO asset_scan_data (\n" +
            "    uuid, asset_uuid, base_line,\n" +
            "\t\tscan_info, creator_uuid,\n" +
            "\t\tcreate_time)\n" +
            "VALUES (\n" +
            "    #{uuid}, #{asset_uuid}, #{base_line}, \n" +
            "\t\t#{scan_info}, #{creator_uuid}, \n" +
            "\t\t#{create_time, jdbcType=TIMESTAMP}\n" +
            "\t\t)")
    int addScanData(AssetScanDataPo scanData);

    /**
     * 读取所有的扫描记录（不含扫描信息）
     * @return
     */
    @Select("SELECT \n" +
            "    uuid, asset_uuid, base_line\n" +
            "\t\tcreator_uuid,\n" +
            "\t\tcreate_time\n" +
            "FROM asset_scan_data")
    List<AssetScanDataPo> getAllScanRecords();

    @Select("SELECT \n" +
            "    sd.uuid,\n" +
            "\t\tsd.asset_uuid,\n" +
            "\t\ta.`name` AS asset_name,\n" +
            "\t\ta.`ip` AS asset_ip,\n" +
            "\t\tsd.base_line,\n" +
            "\t\tsd.creator_uuid,\n" +
            "\t\tu.`name` AS creator_name,\n" +
            "\t\tu.`account` AS creator_account,\n" +
            "\t\tsd.create_time\n" +
            "FROM asset_scan_data sd\n" +
            "INNER JOIN assets a ON a.uuid = sd.asset_uuid\n" +
            "INNER JOIN users u ON u.uuid = sd.creator_uuid\n")
    List<AssetScanRecordDto> getAllScanRecordData();

    @Select("SELECT \n" +
            "    sd.uuid,\n" +
            "\t\tsd.asset_uuid,\n" +
            "\t\tsd.scan_info,\n" +
            "\t\ta.`name` AS asset_name,\n" +
            "\t\ta.`ip` AS asset_ip,\n" +
            "\t\tsd.base_line,\n" +
            "\t\tsd.creator_uuid,\n" +
            "\t\tu.`name` AS creator_name,\n" +
            "\t\tu.`account` AS creator_account,\n" +
            "\t\tsd.create_time\n" +
            "FROM asset_scan_data sd\n" +
            "INNER JOIN assets a ON a.uuid = sd.asset_uuid\n" +
            "INNER JOIN users u ON u.uuid = sd.creator_uuid\n" +
            "WHERE sd.uuid=#{uuid}")
    AssetScanRecordDto getScanInfo(@Param("uuid")String scanUuid);

    @Select("SELECT \n" +
            "    sd.uuid,\n" +
            "\t\tsd.asset_uuid,\n" +
            "\t\ta.`name` AS asset_name,\n" +
            "\t\ta.`ip` AS asset_ip,\n" +
            "\t\tsd.base_line,\n" +
            "\t\tsd.creator_uuid,\n" +
            "\t\tu.`name` AS creator_name,\n" +
            "\t\tu.`account` AS creator_account,\n" +
            "\t\tsd.create_time\n" +
            "FROM asset_scan_data sd\n" +
            "INNER JOIN assets a ON a.uuid =  sd.asset_uuid\n" +
            "INNER JOIN users u ON u.uuid =  sd.creator_uuid\n" +
            "WHERE sd.asset_uuid=#{asset_uuid}" +
            "ORDER BY sd.create_time DESC\n" +
            "LIMIT 0,1")
    AssetScanRecordDto getAssetRecentScanInfo(@Param("asset_uuid")String assetUuid);
}
