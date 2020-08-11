package com.imooc.service.impl;

import com.imooc.enums.CategoryEnum;
import com.imooc.mapper.CategoryMapper;
import com.imooc.mapper.CategoryMapperCustom;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    @Override
    public List<Category> queryAllRootLevelCat() {

        Example example = new Example(Category.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("type", CategoryEnum.FIRST_LEVEL.type);

        List<Category> categories = categoryMapper.selectByExample(example);

        return categories;
    }

    @Override
    public List<CategoryVO> getSubCatList(Integer rootCatId) {

        return categoryMapperCustom.getSubCatList(rootCatId);
    }

    @Override
    public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {
        return categoryMapperCustom.getSixNewItemsLazy(rootCatId);
    }
}
