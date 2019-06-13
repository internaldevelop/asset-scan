package com.toolkit.assetscan.dao.mybatis;

import com.toolkit.assetscan.bean.dto.PolicyDetailInfoDto;
import com.toolkit.assetscan.bean.dto.TaskResultsDto;
import com.toolkit.assetscan.bean.po.PolicyPo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PoliciesMapper {
    /**
     * 新建一条策略记录
     * @param policy 策略的所有参数
     * @return >=1：成功；<=0：失败；
     */
    @Insert("INSERT INTO policies( \n" +
            "uuid, name, code, \n" +
            "group_uuid, type, risk_level, \n" +
            "solutions, create_user_uuid, status, \n" +
            "os_type, baseline, run_mode, \n" +
            "run_contents, consume_time, asset_uuid, \n" +
            "lv1_require, lv2_require, lv3_require, lv4_require, \n" +
            "create_time) \n" +
            "VALUES ( \n" +
            "#{uuid}, #{name}, #{code}, \n" +
            "#{group_uuid}, #{type}, #{risk_level}, \n" +
            "#{solutions}, #{create_user_uuid}, #{status}, \n" +
            "#{os_type}, #{baseline}, #{run_mode}, \n" +
            "#{run_contents}, #{consume_time}, #{asset_uuid}, \n" +
            "#{lv1_require}, #{lv2_require}, #{lv3_require}, #{lv4_require}, \n" +
            "#{create_time, jdbcType=TIMESTAMP}) ")
    int addPolicy(PolicyPo policy);

    /**
     * 获得所有有效的策略记录
     * @return PolicyProps 的集合
     */
    @Select("SELECT * FROM policies p WHERE p.status>=0 ")
    List<PolicyPo> allPolicies();

    /**
     * 根据UUID，获取指定的策略记录
     * @param policyUuid 指定的策略 UUID
     * @return PolicyProps 策略记录的全部数据
     */
    @Select("SELECT * FROM policies p WHERE p.uuid=#{uuid} AND p.status>=0 ")
    PolicyPo getPolicyByUuid(@Param("uuid") String policyUuid);

    /**
     *  根据group uuid获取所在组所有的策略
     * @param policyGroupUuid 指定的策略 UUID
     * @return 根据UUID指定的PolicyProps 策略记录数据
     */
    @Select("SELECT * FROM policies p WHERE p.group_uuid=#{group_uuid} AND p.status>=0 ")
    List<PolicyPo> getPoliciesByGroupUuid(@Param("group_uuid") String policyGroupUuid);

    /**
     *  根据group code获取所在组所有的策略
     * @param policyGroupCode 指定的策略 code
     * @return 根据code指定的PolicyProps 策略记录数据
     */
    @Select("SELECT * FROM policies p WHERE p.code=#{group_code} AND p.status>=0 ")
    List<PolicyPo> getPoliciesByGroupCode(@Param("group_code") String policyGroupCode);

    /**
     * 更新指定的策略记录
     * @param policy 策略的数据
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE policies p SET " +
            "name=#{name}, code=#{code}, group_uuid=#{group_uuid}, \n" +
            "type=#{type}, risk_level=#{risk_level}, solutions=#{solutions}, \n" +
            "create_user_uuid=#{create_user_uuid}, status=#{status}, os_type=#{os_type}, \n" +
            "baseline=#{baseline}, run_mode=#{run_mode}, run_contents=#{run_contents}, \n" +
            "consume_time=#{consume_time}, asset_uuid=#{asset_uuid}, \n" +
            "lv1_require=#{lv1_require}, lv2_require=#{lv2_require}, lv3_require=#{lv3_require}, lv4_require=#{lv4_require}, \n" +
            "create_user_uuid=#{create_user_uuid} \n" +
            "WHERE \n" +
            "p.uuid=#{uuid} AND p.status>=0  ")
    int updatePolicy(PolicyPo policy);

    /**
     * 永久删除一条策略记录
     * @param policyUuid 策略的 UUID
     * @return >=1：成功；<=0：失败；
     */
    @Delete("DELETE FROM policies WHERE uuid=#{uuid} ")
    int deletePolicy(@Param("uuid") String policyUuid);

    /**
     * 更新指定策略记录的状态
     * @param policyUuid 策略的 UUID
     * @param status 新的状态
     * @return >=1：成功；<=0：失败；
     */
    @Update("UPDATE policies p SET " +
            "p.status=#{status} " +
            "WHERE " +
            "p.uuid=#{uuid} ")
    int  updateStatus(@Param("uuid") String policyUuid, @Param("status")int status);

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT(1) AS patch_num,\n" +
            "	GROUP_CONCAT(p.`name`, ':', ter.results, '\\r' ) AS results,\n" +
            "   GROUP_CONCAT(DISTINCT p.`name`, ':', ter.solutions) AS solutions\n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid \n" +
            " GROUP BY a.ip")
    List<TaskResultsDto> patchNotInstalledReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', ter.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinServices' OR pg.`code` = 'LinuxServices') \n" +
            " GROUP BY a.ip")
    List<TaskResultsDto> systemServiceReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', ter.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinSysFileProtect' OR pg.`code` = 'LinuxSysFileProtect') \n" +
            " GROUP BY a.ip")
    List<TaskResultsDto> systemFileServiceReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', ter.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinUserAccountConfig' OR pg.`code` = 'LinuxUserAccountConfig') \n" + //AND pg.`code` = 'UserAccountConfig'
            " GROUP BY a.ip")
    List<TaskResultsDto> userAccountReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', ter.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinUserPwdConfig' OR pg.`code` = 'LinuxUserPwdConfig') \n" + //AND pg.`code` = 'UserPwdConfig'
            " GROUP BY a.ip")
    List<TaskResultsDto> pwdPolicyReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', ter.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinNetworkCommConfig' OR pg.`code` = 'LinuxNetworkCommConfig')  \n" + //AND pg.`code` = 'NetworkCommConfig'
            " GROUP BY a.ip")
    List<TaskResultsDto> networkReport();

    @Select("SELECT\n" +
            "	a.ip AS assets_ip,\n" +
            "	COUNT( 1 ) AS patch_num,\n" +
            "	GROUP_CONCAT( p.`name`, ':', ter.results, '\\\\r' ) AS results,\n" +
            "	GROUP_CONCAT( DISTINCT p.`name`, ':', p.solutions ) AS solutions \n" +
            " FROM\n" +
            "	assets a\n" +
            "	INNER JOIN tasks t ON a.uuid = t.asset_uuid\n" +
            "	INNER JOIN task_execute_results ter ON t.uuid = ter.task_uuid\n" +
            "	INNER JOIN policies p ON ter.policy_uuid = p.uuid\n" +
            "	INNER JOIN policy_groups pg ON pg.uuid = p.group_uuid AND (pg.`code` = 'WinLogAuditConfig' OR pg.`code` = 'LinuxLogAuditConfig') \n" + //AND pg.`code` = 'LogAuditConfig'
            " GROUP BY a.ip")
    List<TaskResultsDto> logReport();

    @Select("SELECT policies.*,\n" +
            "	assets.`name` AS asset_name,\n" +
            "	assets.`ip` AS assets_ip,\n" +
            "	policy_groups.`name` AS group_name\n" +
            " FROM\n" +
            "	policies \n" +
            "	LEFT JOIN assets ON policies.asset_uuid = assets.uuid\n" +
            "	LEFT JOIN policy_groups ON policies.group_uuid = policy_groups.uuid\n")
    List<PolicyDetailInfoDto> getAllPolicyDetailInfos();

    /**
     * 获得所有策略的简要信息
     * @return PolicyProps 的集合
     */
    @Select("SELECT \n" +
            "   uuid, \n" +
            "   name, \n" +
            "   code, \n" +
            "   group_uuid, \n" +
            "   baseline \n" +
            "FROM policies p  \n" +
            "WHERE p.status>=0  \n")
    List<PolicyPo> allPoliciesBrief();
}
