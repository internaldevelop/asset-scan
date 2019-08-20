package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.AssetPerfDataPo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
public interface AssetPerfDataMapper {

    @Select("SELECT\n" +
            "	id,\n" +
            "   uuid,\n" +
            "	asset_uuid,\n" +
            "	cpu_used_percent,\n" +
            "	memory_used_percent,\n" +
            "	disk_used_percent,\n" +
            "	create_time \n" +
            "FROM\n" +
            "	asset_perf_data a \n" +
            "WHERE\n" +
            "	a.asset_uuid = #{asset_uuid} \n" +
            "   and a.create_time BETWEEN #{begin_time} AND #{end_time} \n" +
            "ORDER BY\n" +
            "	a.create_time")
    List<AssetPerfDataPo> getHistoryPerfinfo(@Param("asset_uuid") String assetUuid, @Param("begin_time")Timestamp beginTime, @Param("end_time")Timestamp endTime);

    @Select("SELECT\n" +
            "	id,\n" +
            "   uuid,\n" +
            "	asset_uuid,\n" +
            "	cpu_used_percent,\n" +
            "	memory_used_percent,\n" +
            "	disk_used_percent,\n" +
            "	create_time \n" +
            "FROM\n" +
            "	asset_perf_data a \n" +
            "WHERE\n" +
            "	a.asset_uuid = #{asset_uuid} \n" +
            "ORDER BY a.id  DESC LIMIT 1")
    AssetPerfDataPo getAssetPerfInfo(@Param("asset_uuid") String assetUuid);
}
