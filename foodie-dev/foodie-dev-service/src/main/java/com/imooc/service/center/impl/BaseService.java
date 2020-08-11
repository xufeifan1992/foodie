package com.imooc.service.center.impl;

import com.github.pagehelper.PageInfo;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public class BaseService {

    public PagedGridResult setterPagedGrid(Integer page, List<?> list) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        //当前页
        grid.setPage(page);
        //总页数
        grid.setTotal(pageList.getPages());
        //每行显示内容
        grid.setRows(list);
        //总行数
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
