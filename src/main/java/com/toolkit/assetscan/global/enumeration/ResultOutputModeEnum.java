package com.toolkit.assetscan.global.enumeration;

public enum ResultOutputModeEnum {
    MODE_PDF(1),////运行结果输出模式：1：pdf模式；2：Excel模式；3：Word模式；4：HTML模式
    MODE_EXCEL(2),
    MODE_WORD(3),
    MODE_HTML(4),
    ;

    private int resultOutputMode;

    ResultOutputModeEnum(int resultOutputMode) {
        this.resultOutputMode = resultOutputMode;
    }

    public int getResultOutputMode() {
        return resultOutputMode;
    }

    public void setResultOutputMode(int resultOutputMode) {
        this.resultOutputMode = resultOutputMode;
    }
}
