package com.imooc.pojo.vo;

public class SubCategoryVO {
    private Integer subId;
    private String subName;
    private Integer subType;
    private Integer subFatherId;

    public Integer getSubId() {
        return subId;
    }

    public void setSubId(Integer subId) {
        this.subId = subId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public Integer getSubType() {
        return subType;
    }

    public void setSubType(Integer subType) {
        this.subType = subType;
    }

    public SubCategoryVO() {
    }

    public SubCategoryVO(Integer subId, String subName, Integer subType) {
        this.subId = subId;
        this.subName = subName;
        this.subType = subType;
    }


}
