package com.imooc.service;

import com.imooc.pojo.Stu;


public interface StuService {

    public Stu getStuInfo(int id);

    public void saveStu(Stu stu);

    public void updateStu(int id);

    public void deleteStu(int id);

    void saveParent();

    void saveChildren();
}
