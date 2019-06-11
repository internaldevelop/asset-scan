package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.po.PasswordPo;
import com.toolkit.assetscan.bean.po.UserPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 重要说明
 * users.status >= 0，为激活、未激活、临时失效等活动状态，小于0为逻辑删除或永久失效等消亡状态
 * 本接口只允许开放 users.status >= 0 的用户记录操作的接口
 */
@Component
public interface UsersMapper {
    /**
     * 读取所有用户记录
     * @return 成功时返回 UserProps 的列表，失败时返回 null
     */
    @Select("SELECT * FROM users u WHERE u.status>=0 ")
    List<UserPo> allUsers();

    /**
     * 获取用户记录
     * @param uuid 指定的用户 UUID
     * @return UserProps 用户记录数据
     */
    @Select("SELECT * FROM users u WHERE uuid=#{uuid} AND u.status>=0 ")
    UserPo getUserByUuid(@Param("uuid") String uuid);

    /**
     * 获取指定账号名的 UUID
     * @param account 用户的账号
     * @return 用户的 UUID
     */
    @Select("SELECT uuid FROM users u WHERE u.account=#{account} AND u.status>=0 ")
    String getUserUuidByAccount(@Param("account") String account);

    /**
     * 添加一条用户记录
     * @param userPo 用户属性，和 users 表对应，除自增主键 id，包含其它所有字段
     * @return >=1：成功；<=0：失败；
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
    int addUser(UserPo userPo);

    /**
     * 更新用户记录，但不更新密码
     * 1. 少数应用场景下，有前置操作，需要把该用户记录读取出来，更新相关字段，再调用更新
     * 2. 多数应用场景下，该条记录已读取在仓库或缓存中，只需提取出来更新相关字段后，即可更新
     * @param userPo 用户属性，和 users 表对应，除自增主键 id，包含其它所有字段
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE users u SET " +
                "uuid=#{uuid}, account=#{account}, " +
                "status=#{status}, name=#{name}, address=#{address}, email=#{email}, " +
                "phone=#{phone}, description=#{description}, user_group=#{user_group}," +
                "expire_time=#{expire_time, jdbcType=TIMESTAMP}, create_time=#{create_time, jdbcType=TIMESTAMP} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int updateUserByUuid(UserPo userPo);

    /**
     * 修改用户密码
     * @param userUuid 用户的 UUID
     * @param password 用户的新密码
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE users u SET " +
                "password=#{password} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int changePassword(@Param("uuid") String userUuid, @Param("password")String password);

    /**
     * 更新用户密码的剩余尝试次数
     * @param userUuid 用户的 UUID
     * @param pwd_rat 新的剩余尝试次数
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE users u SET " +
                "pwd_rat=#{pwd_rat} " +
            "WHERE " +
                "uuid=#{uuid} AND u.status>=0  ")
    int updateRAT(@Param("uuid") String userUuid, @Param("pwd_rat")int pwd_rat);

    /**
     * 获取指定用户的密码相关参数
     * @param userUuid 用户的 UUID
     * @return PasswordProps 指定用户的密码参数
     */
    @Select("SELECT uuid AS user_uuid, password, pwd_mat, pwd_rat, user_group FROM users u WHERE uuid=#{uuid} AND u.status>=0")
    PasswordPo getPasswordByUuid(@Param("uuid") String userUuid);

    /**
     * 获取系统中指定账户名的数量，用于查重
     * @param account 指定账户名
     * @return 数据库中指定账户的数量
     */
    @Select("SELECT count(*) from users u where  u.account=#{account} AND u.status>=0")
    int getExistAccountCount(@Param("account") String account);

    /**
     * 更新指定用户记录的状态
     * @param userUuid 指定用户的 UUID
     * @param status 新的状态
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE users u SET " +
            "u.status=#{status} " +
            "WHERE " +
            "uuid=#{uuid} ")
    int updateStatus(@Param("uuid") String userUuid, @Param("status")int status);
}
