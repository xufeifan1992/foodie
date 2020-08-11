package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;

    @Override
    public void saveParent() {
        Stu stu = new Stu();
        stu.setAge(28);
        stu.setName("许非凡");
        stuMapper.insert(stu);
    }



    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void saveChildren() {
        Stu stu = new Stu();
        stu.setAge(28);
        stu.setName("王薪然");
        stuMapper.insert(stu);
        int a = 1 / 0;
    }



    @Override
    public Stu getStuInfo(int id) {

        return stuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void saveStu(Stu stu) {

    }

    @Override
    public void updateStu(int id) {

    }

    @Override
    public void deleteStu(int id) {

    }
}
