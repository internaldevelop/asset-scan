package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.BaseLinePo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BaseLineMapper {
    /**
     * 基线模板的数量
     * @return
     */
    @Select("SELECT COUNT(*) FROM base_lines\n")
    int getCount();

    /**
     * 所有基线模板
     * @return
     */
    @Select("SELECT id, level, templates FROM base_lines\n")
    List<BaseLinePo> getAllBaseLines();

    /**
     * 获取指定等级的基线
     * @param level
     * @return
     */
    @Select("SELECT \n" +
            "    id, level, templates \n" +
            "FROM base_lines\n" +
            "WHERE level=#{level}\n")
    BaseLinePo getBaseLine(int level);

    /**
     * 更新指定等级的基线模板
     * @param level
     * @param templates
     * @return
     */
    @Update("UPDATE base_lines b SET \n" +
            "    templates=#{templates} \n" +
            "WHERE level=#{level}\n")
    int updateTemplate(@Param("level")int level, @Param("templates")String templates);
}
