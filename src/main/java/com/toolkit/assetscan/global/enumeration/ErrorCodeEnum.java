package com.toolkit.assetscan.global.enumeration;

public enum ErrorCodeEnum {
    ERROR_OK(0, "OK"),
    ERROR_NOT_DATA(200, "成功无数据"),
    ERROR_GENERAL_ERROR(1001, "未知错误"),
    ERROR_INTERNAL_ERROR(1002, "内部错误"),
    ERROR_PARAMETER(1003, "参数错误"),
    ERROR_NOT_IMPLEMENTED(1004, "待实现"),
    ERROR_INVALID_USER(1005, "无效用户"),
    ERROR_USER_NOT_FOUND(1006, "用户未找到"),
    ERROR_APP_NOT_FOUND(1009, "应用未找到"),
    ERROR_USER_INACTIVE(1010, "用户未激活"),
    ERROR_POLICY_NOT_FOUND(1011, "策略未找到"),
    ERROR_APP_INACTIVE(1012, "应用未激活"),
    ERROR_USER_NOT_ENROLLED(1013, "用户未绑定"),
    ERROR_AUTH_FAILED(1014, "认证失败"),
    ERROR_INVALID_PASSWORD(1015, "口令错误"),
    ERROR_INVALID_OTP(1016, "OTP错误"),
    ERROR_PUSH_NOT_REACH(1017, "PUSH未成功"),
    ERROR_USER_REJECTED(1018, "用户拒绝授权"),
    ERROR_INVALID_USER_CERT(1019, "非法用户证书"),
    ERROR_INVALID_DEVICE_CERT(1020, "非法设备证书"),
    ERROR_USERNAME_USED(1021, "该用户名已被注册"),
    ERROR_DEVICE_NOT_SUPPORT(1022, "不支持该设备"),
    ERROR_NEED_PARAMETER(1023, "参数不足"),
    ERROR_NO_DEVICE(1024, "没有对应的设备"),
    ERROR_CHALLENGE_EXPIRED(1025, "挑战码失效"),
    ERROR_INVALID_TOKEN(1026, "错误的TOKEN"),
    ERROR_ALREADY_ENROLLED(1027, "已经绑定"),
    ERROR_USER_REGISTERED(1028, "用户已注册"),
    ERROR_USER_NOT_REACHED(1030, "用户未打开客户端"),
    ERROR_USER_NOT_ACTIVATED(1031, "用户未激活"),
    ERROR_CANNOT_ENROLL(1032, "无法绑定"),
    ERROR_USER_PASSWORD_LOCKED(1033, "用户口令已锁定"),
    ERROR_TOKEN_EXPIRED(1035, "TOKEN已失效"),
    ERROR_INVALID_VERIFY_CODE(1036, "错误的验证码"),
    ERROR_USER_NOT_AUTHENTICATED(1037, "用户未登录"),
    ERROR_DECRYPT_FAILED(1038, "解密失败"),
    ERROR_NEED_REALNAMEINFO(1039, "实名信息不足"),
    ERROR_INVALID_ACCOUNT(1040, "错误的账号"),
    ERROR_ACCOUNT_NOT_AUTHED(1041, "用户未授权"),
    ERROR_REQUEST_TOO_OFTEN(1042, "请求过于频繁"),
    ERROR_INVALID_PICTURE(1043, "错误的图片"),
    ERROR_PICTURE_NOT_MATCH(1044, "图片数据和实名信息不符合"),
    ERROR_EXPIRED_PICTURE(1045, "证件已过期"),
    ERROR_TIME_OUT(1053, "超时"),
    ERROR_NEED_INFO(1054, "需要补充信息"),
    ERROR_WRONG_INFO(1055, "补充信息错误"),
    ERROR_ADMIT_CLEAR_AUTH_FAIL_COUNT(2001, "允许清除验证失败次数"),
    ERROR_NEED_APP_CALLBACK_URL(2002, "需要应用回调的URL链接"),
    ERROR_FAIL_CALLBACK(2003, "回调失败"),
    ERROR_TASK_NAME_EXIST(2004, "任务名称已存在"),
    ERROR_INTERFACE_NOT_FOUND(2005, "请求接口不存在"),
    ERROR_TASK_NOT_FOUND(2030, "任务未找到"),
    ERROR_TASK_INFO_NOT_FOUND(2031, "任务信息未找到"),
    ERROR_GROUP_NOT_FOUND(2032, "策略组未找到"),
    ERROR_INCORRECT_TASK_RUN_STATUS(2034, "任务运行状态错误"),
    ERROR_TASK_RUN_STATUS_NOT_FOUND(2035, "无法获取任务的运行状态"),
    ERROR_PROJECT_NOT_FOUND(2036, "项目未找到"),
    ERROR_TIME_INCORRECT(2037, "指定的时间不符合要求"),
    ERROR_TIME_AFTER_CURRENT(2038, "指定时间晚于当前时间"),
    ERROR_NO_RESULT_HISTORY(2039, "没有找到扫描历史记录"),
    ERROR_NO_EXEC_ACTIONS(2040, "没有找到操作日志"),
    ERROR_SCHEDULER_FAILED(2041, "定时计划任务启动失败"),
    ERROR_SCHEDULE_TASK_NOT_FOUND(2042, "未找到指定的计划任务"),
    ERROR_SCHEDULER_NOT_START(2043, "计划任务尚未启动"),
    ERROR_ASSET_EMPTY(2044, "资产列表为空"),
    ERROR_ASSET_NOT_FOUND(2045, "资产未找到"),
    ERROR_ASSET_NAME_EXIST(2046, "资产名已存在"),
    ERROR_FAILED_ADD_ASSET(2047, "添加资产失败"),
    ERROR_FAILED_UPDATE_ASSET(2047, "更新资产信息失败"),
    ERROR_POLICY_NAME_EXIST(2048, "策略名已存在"),
    ERROR_PROJECT_NAME_EXIST(2049, "项目名称已存在"),
    ;

    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public Integer getCode() {
        return code;
    }

    public String getCodeString() {
        return Integer.toString(this.code);
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
