package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.PasswordProps;
import com.toolkit.assetscan.bean.UserProps;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
/**
 * 重要说明
 * users.status >= 0，为激活、未激活、临时失效等活动状态，小于0为逻辑删除或永久失效等消亡状态
 * 本接口只允许开放 users.status >= 0 的用户记录操作的接口
 */
public interface UsersMapper {
    /**
     * 读取所有用户记录
     * @return 成功时返回 UserProps 的列表，失败时返回 null
     */
    @Select("SELECT * FROM users u WHERE u.status>=0 ")
    List<UserProps> allUsers();

    @Select("SELECT * FROM users u WHERE uuid=#{uuid} AND u.status>=0 ")
    UserProps getUserByUuid(@Param("uuid") String uuid);

    @Select("SELECT uuid FROM users u WHERE u.account=#{account} AND u.status>=0 ")
    String getUserUuidByAccount(@Param("account") String account);

    /**
     * 添加一条用户记录
     * @param userProps 用户属性，和 users 表对应，除自增主键 id，包含其它所有字段
     * @return 1：成功；0：失败；其它：未知错误
     */
    @Insert("INSERT INTO users( " +
                "uuid, account, password, " +
                "pwd_mat, pwd_rat, " +
                "status, name, address, email, " +
                "phone, description, user_group, " +
                "expire_time, create_time) " +
            "VALUES ( " +
                "#{uuid}, #{account}, #{password}, " +
                "#{pwd_mat}, #{pwd_rat}, " +
                "#{status}, #{name}, #{address}, #{email}, " +
                "#{phone}, #{description}, #{user_group}, " +
                "#{expire_time, jdbcType=TIMESTAMP}, #{create_time, jdbcType=TIMESTAMP}) ")
    int addUser(UserProps userProps);

    /**
     * 更新用户记录，但不更新密码
     * 1. 少数应用场景下，有前置操作，需要把该用户记录读取出来，更新相关字段，再调用更新
     * 2. 多数应用场景下，该条记录已读取在仓库或缓存中，只需提取出来更新相关字段后，即可更新
     * @param userProps 用户属性，和 users 表对应，除自增主键 id，包含其它所有字段
     * @return 1：成功；0：失败；其它：未知错误
     */
    @Update("UPDATE users u SET " +
                "uuid=#{uuid}, account=#{account}, " +
                "status=#{status}, name=#{name}, address=#{address}, email=#{email}, " +
                "phone=#{phone}, description=#{description}, user_group=#{user_group}," +
                "expire_time=#{expire_time, jdbcType=TIMESTAMP}, create_time=#{create_time, jdbcType=TIMESTAMP} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int updateUserByUuid(UserProps userProps);

    @Update("UPDATE users u SET " +
                "password=#{password} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int changePassword(@Param("uuid") String userUuid, @Param("password")String password);

    @Update("UPDATE users u SET " +
                "pwd_rat=#{pwd_rat} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int updateRAT(@Param("uuid") String userUuid, @Param("pwd_rat")int pwd_rat);

    @Select("SELECT uuid AS user_uuid, password, pwd_mat, pwd_rat FROM users u WHERE uuid=#{uuid} AND u.status>=0")
    PasswordProps getPasswordByUuid(@Param("uuid") String userUuid);

    @Select("SELECT count(*) from users u where  u.account=#{account} AND u.status>=0")
    int getExistAccountCount(@Param("account") String account);
}
