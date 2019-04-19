package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.AssetPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AssetsMapper {
    /**
     * 新建一条资产记录
     */
    @Insert("INSERT INTO assets( " +
            "uuid, name, code, " +
            "ip, port, user, " +
            "password, os_type, " +
            "os_ver, create_user_uuid, " +
            "create_time) " +
            "VALUES ( " +
            "#{uuid}, #{name}, #{code}, " +
            "#{ip}, #{port}, #{user}," +
            "#{password}, #{os_type}, " +
            "#{os_ver}, #{create_user_uuid}, " +
            "#{create_time, jdbcType=TIMESTAMP}) ")
    int addAsset(AssetPo assetPo);

    /**
     * 获得所有的资产记录
     * @return
     */
    @Select("SELECT * FROM assets ")
    List<AssetPo> getAllAssets();

    /**
     * 更新指定资产信息
     * @param assetPo
     * @return
     */
    @Update("UPDATE assets a SET " +
            "name=#{name}, code=#{code}, " +
            "ip=#{ip}, port=#{port}, " +
            "user=#{user}, password=#{password}, " +
            "create_user_uuid=#{create_user_uuid}, os_ver=#{os_ver} " +
            "WHERE " +
            "a.uuid=#{uuid} ")
    int updateAsset(AssetPo assetPo);

    /**
     * 删除指定的一条资产
     * @param assetPo
     * @return
     */
    @Delete("DELETE FROM assets WHERE uuid=#{uuid} ")
    int deleteAsset(AssetPo assetPo);

    /**
     * 根据UUID获取指定资产记录
     * @param assetUuid
     * @return
     */
    @Select("SELECT * FROM assets a WHERE a.uuid=#{uuid}")
    AssetPo getAssetByUuid(@Param("uuid") String assetUuid);
}
