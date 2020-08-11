package com.imooc.service;

import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;

import java.util.List;

public interface CategoryService {

    /**
     * 查询所有一级分类
     */
    public List<Category> queryAllRootLevelCat();


    /**
     * 根据一级分类ID查询子分类˙
     */
    public List<CategoryVO> getSubCatList(Integer rootCatId);

    /**
     * 查询首页一级分类下的六条最新商品
     */
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
