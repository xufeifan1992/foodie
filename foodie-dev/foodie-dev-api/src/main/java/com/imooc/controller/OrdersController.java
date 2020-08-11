package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "订单相关接口", tags = {"订单相关接口"})
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;


    @ApiOperation(value = "创建订单", notes = "创建订单", httpMethod = "POST")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request, HttpServletResponse response) {

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type || submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type) {
            IMOOCJSONResult.errorMsg("不支持此付款方式");
        }

        //1.创建订单
        OrderVO order = orderService.createOrder(submitOrderBO);


        //2.创建订单以后，移除购物车中已结算的商品
        //TODO 整合redis以后，完善购物车中的已结算商品清楚，并且同步到前端cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "");
        //3.向支付中心发送当前订单，用于保存支付中心订单数据
        MerchantOrdersVO merchantOrdersVO = order.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId","imooc");
        headers.add("password","imooc");

        HttpEntity<MerchantOrdersVO> httpEntity = new HttpEntity(MerchantOrdersVO.class,headers);

        ResponseEntity<IMOOCJSONResult> responseEntity = restTemplate.postForEntity(paymentUrl, httpEntity, IMOOCJSONResult.class);
        IMOOCJSONResult result = responseEntity.getBody();
        if(result.getStatus() != 200){
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败");
        }


        return IMOOCJSONResult.ok(order);
    }

    @PostMapping("notifyMerhchantOrderPaid")
    public IMOOCJSONResult notifyMerhchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);

        return IMOOCJSONResult.ok();

    }

    @PostMapping("getPaidOrderInfo")
    public IMOOCJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);


        return IMOOCJSONResult.ok(orderStatus);

    }

}