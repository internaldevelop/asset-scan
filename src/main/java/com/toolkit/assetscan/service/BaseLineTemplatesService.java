package com.toolkit.assetscan.service;

import com.toolkit.assetscan.bean.po.BaseLinePo;
import com.toolkit.assetscan.dao.mybatis.BaseLineMapper;
import com.toolkit.assetscan.global.bean.ResponseBean;
import com.toolkit.assetscan.global.enumeration.ErrorCodeEnum;
import com.toolkit.assetscan.global.response.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseLineTemplatesService {
    @Autowired
    BaseLineMapper baseLineMapper;
    @Autowired
    ResponseHelper responseHelper;

    public ResponseBean queryBaseLines(int level) {
        List<BaseLinePo> baseLines;
        if (level <= 0) {
            baseLines = baseLineMapper.getAllBaseLines();
            if (baseLines == null)
                return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
        } else {
            baseLines = new ArrayList<>();
            BaseLinePo baseLinePo = baseLineMapper.getBaseLine(level);
            if (baseLinePo == null)
                return responseHelper.error(ErrorCodeEnum.ERROR_BASE_LINE_NOT_FOUND);
            baseLines.add(baseLinePo);
        }
        return responseHelper.success(baseLines);
    }
}
