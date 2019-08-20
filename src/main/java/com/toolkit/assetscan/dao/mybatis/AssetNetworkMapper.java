package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.AssetNetWorkPo;
import com.toolkit.assetscan.bean.po.AssetPerfDataPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public interface AssetNetworkMapper {

    @Insert("INSERT INTO asset_network_data (\n" +
            "	`uuid`,\n" +
            "	`asset_uuid`,\n" +
            "	`connect_ip`,\n" +
            "	`connect_flag`,\n" +
            "	`connect_time`,\n" +
            "	`url`,\n" +
            "	`url_duration`,\n" +
            "	`url_time`,\n" +
            "	`delay`,\n" +
            "	`delay_time`,\n" +
            "	`throughput`,\n" +
            "	`throughput_time`,\n" +
            "	`bandwidth`,\n" +
            "	`bandwidth_time`,\n" +
            "	`creator_uuid`,\n" +
            "	`create_time` \n" +
            ")\n" +
            "VALUES(\n" +
            "	#{uuid},\n" +
            "	#{asset_uuid},\n" +
            "	#{connect_ip},\n" +
            "	#{connect_flag},\n" +
            "	#{connect_time},\n" +
            "	#{url},\n" +
            "	#{url_duration},\n" +
            "	#{url_time},\n" +
            "	#{delay},\n" +
            "	#{delay_time},\n" +
            "	#{throughput},\n" +
            "	#{throughput_time},\n" +
            "	#{bandwidth},\n" +
            "	#{bandwidth_time},\n" +
            "	#{creator_uuid},\n" +
            "	#{create_time}\n" +
            ")")
    int addNetWOrkData(AssetNetWorkPo anwPo);

    @Select("SELECT * FROM asset_network_data WHERE asset_uuid = #{asset_uuid} ORDER BY id DESC LIMIT 1")
    AssetNetWorkPo getNetWorkinfo(@Param("asset_uuid") String assetUuid);
}
