package com.imooc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/set")
    public Object set(String key){
        redisTemplate.opsForValue().set(key,"imooc");
        return "Redis set";

    }
    @GetMapping("/get")
    public Object get(String key){
        String s = (String) redisTemplate.opsForValue().get(key);
        return "Redis get";

    }
    @GetMapping("/delete")
    public Object delete(String key){
        Boolean delete = redisTemplate.delete(key);
        return "Redis delete";

    }
}