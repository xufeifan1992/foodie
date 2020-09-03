package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.*;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.sf.jsqlparser.expression.UserVariable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        //实现用户redis会话--token
        UsersVO usersVO = convertUsersVO(users);


        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        
        synchShopcartDate(users.getId(),request,response);

        return IMOOCJSONResult.ok();
    }


    private UsersVO convertUsersVO(Users users){
        //实现用户redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
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

        //实现用户redis会话--token
        UsersVO usersVO = convertUsersVO(users);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        synchShopcartDate(users.getId(),request,response);

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "退出登录", notes = "退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        //清除用户的相关信息cookie
        CookieUtils.deleteCookie(request,response,"user");

        //用户退出登录，需要清空购物车
        //分布式会话中需要清除用户数据
        CookieUtils.deleteCookie(request,response,FOODIE_SHOPCART);

        //清空token
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        

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
                /**
                 * 1.已经存在的，把cookie中对应的数量，覆盖redis
                 * 2.该项商品标记待删除，统一放入一个待删除的list
                 * 3.从coolie中清理所有的待删除list
                 * 4.合并redis和coolie中的待删除list
                 * 5.更新redis和cookie中
                 */
                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartRedisJson,ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie,ShopcartBO.class);

                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();
                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = redisShopcart.getSpecId();

                        if(redisSpecId.equals(cookieSpecId)){
                            //覆盖购买数量。不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            //把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                //从现有cookie中删除对应覆盖过得商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                //合并redis和cookie
                shopcartListRedis.addAll(shopcartListCookie);

                //更新到redis和cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartListRedis),true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId,JsonUtils.objectToJson(shopcartListRedis));

            }else {
                //redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request,response,FOODIE_SHOPCART,shopcartRedisJson,true);
            }
        }

    }
}
