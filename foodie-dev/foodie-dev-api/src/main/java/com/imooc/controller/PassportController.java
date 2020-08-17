package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
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
public class PassportController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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
        
        synchShopcartDate(users.getId(),request,response);

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

        synchShopcartDate(users.getId(),request,response);

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

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartDate(String userId,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        /**
         * 1.如果redis中无数据, 如果cookie中的购物车为空，那么这个时候不做任何处理
         *                    如果cookie中的购物车不为空，此时直接放入redis中
         * 2.如果redis中有数据，如果cookie中的购物车为，那么直接把redis购物车覆盖本地rookie
         *                   如果cookie中购物车不为空，如果cookie中的某个商品在redis中存在，则以cookie为主，删除redis中的数据，重新添加
         * 3.同步到redis中取了以后，覆盖本地cookie购物车数据，保证本地购物车数据是同步的
         */

        //从redis中获取购物车
        String shopcartRedisJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);


        //从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request,FOODIE_SHOPCART,true);

        if(StringUtils.isNotBlank(shopcartRedisJson)){
            //redis为空，cookie不为空，直接把cookie中的数据放入redis
            redisOperator.set(FOODIE_SHOPCART,shopcartStrCookie);
        }else {
            //redis不为空，cookie不为空，合并cookie和redis中的购物车数据，同一个商品覆盖redis
            if(StringUtils.isNotBlank(shopcartStrCookie)){

            }else {
                //redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,shopcartRedisJson,true);
            }
        }

    }
}
