package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    public static final String payReturnUrl = "http://192.168.133.129:8088/foodie-dev-api/orders/notifyMerhchantOrderPaid";

    //支付中心调用地址
    public static final String paymentUrl = "http://payment.t.mykewang.com/foodie-payment/payment/createMerchantOrder";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private MyOrdersService myOrdersService;

    @Autowired
    private RedisOperator redisOperator;

    //上传头像地址
    public static final String IMAGE_USER_FACE_LOCATION = File.separator
            + "Users" + File.separator
            + "xu"
            + File.separator
            + "Documents"
            + File.separator
            + "temp"
            + File.separator
            + "foodie"
            + File.separator + "face";

    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders orders = myOrdersService.queryMyOrder(orderId, userId);
        if (orderId == null) {
            return IMOOCJSONResult.errorMsg("订单不存在");
        }

        return IMOOCJSONResult.ok(orders);

    }

    public UsersVO convertUsersVO(Users users){
        //实现用户redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }
}