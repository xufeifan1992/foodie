package com.imooc.controller;

import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class HelloWorld {
    @Autowired
    private StuService stuService;

    @GetMapping("/getStu/{id}")
    public Object getStu(@PathVariable("id") int id){
        Stu stuInfo = stuService.getStuInfo(id);
        return stuInfo;

    }
}