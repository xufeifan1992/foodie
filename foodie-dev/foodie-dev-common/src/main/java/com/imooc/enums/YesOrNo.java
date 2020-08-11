package com.imooc.enums;

/**
 * 是否枚举
 */
public enum YesOrNo {

    no(0, "否"),
    yes(1, "是");

    public final Integer type;

    public final String value;

    YesOrNo(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
