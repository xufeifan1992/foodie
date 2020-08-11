package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {
    public static final String FOODIE_SHOPCART = "shopcart";
    public static final Integer COMMON_PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE = 20;

    public static final String payReturnUrl = "http://192.168.133.129:8088/foodie-dev-api/orders/notifyMerhchantOrderPaid";

    //支付中心调用地址
    public static final String paymentUrl = "http://payment.t.mykewang.com/foodie-payment/payment/createMerchantOrder";

    @Autowired
    private MyOrdersService myOrdersService;

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
}