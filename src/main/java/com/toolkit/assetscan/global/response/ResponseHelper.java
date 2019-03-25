package com.toolkit.assetscan.global.response;

import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import org.springframework.stereotype.Component;

@Component
public class ResponseHelper {
    public ResponseBean error(ErrorCodeEnum err) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(err.getCode());
        responseBean.setError(err.getMsg());
        responseBean.setPayload(null);
        return responseBean;
    }

    public ResponseBean success() {
        return success(null);
    }

    public ResponseBean success(Object data) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(ErrorCodeEnum.ERROR_OK.getCode());
        responseBean.setError(ErrorCodeEnum.ERROR_OK.getMsg());
        responseBean.setPayload(data);
        return responseBean;
    }
}
