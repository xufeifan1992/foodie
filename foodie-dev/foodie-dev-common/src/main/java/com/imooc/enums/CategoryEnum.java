package com.imooc.enums;

public enum CategoryEnum {

    FIRST_LEVEL(1,"一级分类"),
    SECOND_LEVEL(2,"二级分类"),
    THIRD_LEVEL(3,"三级分类");

    public final Integer type;

    public final String name;

    CategoryEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
