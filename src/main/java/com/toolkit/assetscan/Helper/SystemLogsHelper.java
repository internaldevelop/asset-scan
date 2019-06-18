package com.toolkit.assetscan.Helper;

import com.toolkit.assetscan.bean.dto.SystemLogsDto;
import com.toolkit.assetscan.bean.po.SystemLogPo;
import com.toolkit.assetscan.dao.mybatis.SystemLogsMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.params.Const;
import com.toolkit.assetscan.global.response.ResponseHelper;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class SystemLogsHelper {

    @Autowired private SystemLogsMapper systemLogsMapper;
    @Autowired
    private HttpServletRequest httpServletRequest;

    public final int SUCCESS = 1;
    public final int FAIL = 2;
    public final int SYS_ERROR = 3;
    public final int INFO = 4;
    public final int EXCEPT = 5;
    public final int WARNING = 6;

    public boolean addLog(int type, String title, String contents) {
        SystemLogPo systemLogPo = new SystemLogPo();
        systemLogPo.setUuid(MyUtils.generateUuid());
        systemLogPo.setType(type);
        systemLogPo.setTitle(title);
        systemLogPo.setContents(contents);
        systemLogPo.setCreate_time(MyUtils.getCurrentSystemTimestamp());
        systemLogPo.setCreate_user_uuid((String) httpServletRequest.getSession().getAttribute(Const.USER_UUID));
        int ret = systemLogsMapper.addLog(systemLogPo);
        return (ret == 1);
    }

    public boolean success(String title, String contents) {
        return addLog(SUCCESS, title, contents);
    }
    public boolean fail(String title, String contents) {
        return addLog(FAIL, title, contents);
    }
    public boolean sysError(String title, String contents) {
        return addLog(SYS_ERROR, title, contents);
    }
    public boolean info(String title, String contents) {
        return addLog(INFO, title, contents);
    }
    public boolean exception(String title, String contents) {
        return addLog(EXCEPT, title, contents);
    }
    public boolean warning(String title, String contents) {
        return addLog(WARNING, title, contents);
    }

    public void logEvent(ResponseBean responseBean, String title, String contents) {
        if (responseBean.getCode() == ErrorCodeEnum.ERROR_OK.getCode()) {
            success(title, contents + "成功");
        } else {
            fail(title, contents + "失败，错误码：" + responseBean.getCode());
        }
    }
}
