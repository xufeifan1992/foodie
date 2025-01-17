package com.imooc.pojo.vo;

import java.util.List;

public class CategoryVO {

    private Integer id;
    private String name;
    private String type;
    private Integer fatherId;

    private List<SubCategoryVO> subCatList;

    public CategoryVO() {
    }

    public CategoryVO(Integer id, String name, String type, Integer fatherId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.fatherId = fatherId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getFatherId() {
        return fatherId;
    }

    public void setFatherId(Integer fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVO> getSubCatList() {
        return subCatList;
    }

    public void setSubCatList(List<SubCategoryVO> subCatList) {
        this.subCatList = subCatList;
    }
}