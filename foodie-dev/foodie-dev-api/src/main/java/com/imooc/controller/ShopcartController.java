package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车相关接口", tags = {"购物车相关接口"})
@RequestMapping("/shopcart")
@RestController
public class ShopcartController {

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopcartBO shopcartBO,
                               HttpServletRequest request,
                               HttpServletResponse response
    ) {

        if(StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }
        //TODO 前端用户在登陆的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        return IMOOCJSONResult.ok();

    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(@RequestParam String userId,
                               @RequestBody String itemSpecId,
                               HttpServletRequest request,
                               HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg("");
        }
        if (StringUtils.isBlank(itemSpecId)) {
            return IMOOCJSONResult.errorMsg("");
        }
        //TODO 用户在页面删除购物车数据，如果此时用户已经登录，则需要同步删除redis中的数据
        return IMOOCJSONResult.ok();

    }
}