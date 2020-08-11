package com.imooc.enums;

public enum CommentLevel {
    good(1, "好评"),
    normal(2, "中评"),
    bad(3, "差评");

    public final Integer type;

    public final String comment;

    CommentLevel(Integer type, String comment) {
        this.type = type;
        this.comment = comment;
    }
}
