package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录接口", tags = "用于注册登录接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "校验用户名是否存在", notes = "校验用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
        //判断用户名不能为空
        if (StringUtils.isEmpty(username)) {
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        //查询注册用户名是否存在
        boolean boo = userService.queryUsernameIsExit(username);
        if (boo) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        return IMOOCJSONResult.ok();
    }



    @ApiOperation(value = "注册接口", notes = "注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPass = userBO.getConfirmPassword();

        //判断用户名和密码不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPass)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        //查询注册用户名是否存在
        boolean boo = userService.queryUsernameIsExit(username);
        if (boo) {
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }

        //密码长度必须大于六位
        if (password.length() < 6) {
            return IMOOCJSONResult.errorMsg("密码长度必须大于六位");
        }

        //两次密码是否一致
        if (!password.equals(confirmPass)) {
            return IMOOCJSONResult.errorMsg("两次密码是不一致");
        }


        Users users = userService.creatUser(userBO);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(users), true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "登录接口", notes = "登录接口", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //判断用户名和密码不为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }

        Users users = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if(users == null){
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }


        //将返回对象部分属性设置为null
        setNullProperty(users);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(users), true);

        //TODO 生成用户token，存入redis会话
        //TODO 同步购物车数据

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "退出登录", notes = "退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        CookieUtils.deleteCookie(request,response,"user");

        return IMOOCJSONResult.ok();
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}