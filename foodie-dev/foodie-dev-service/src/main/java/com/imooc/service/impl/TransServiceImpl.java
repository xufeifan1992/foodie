package com.imooc.service.impl;

import com.imooc.service.StuService;
import com.imooc.service.TransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransServiceImpl implements TransService {

    @Autowired
    private StuService stuService;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save() {
        stuService.saveParent();
        try {
            //savePoint
            stuService.saveChildren();
        }catch (RuntimeException e){
            System.out.println("出现异常");
        }
    }



}

