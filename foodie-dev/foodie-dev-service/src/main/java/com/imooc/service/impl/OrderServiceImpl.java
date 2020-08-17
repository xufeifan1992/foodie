package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Override
    public OrderVO createOrder(SubmitOrderBO submitOrderBO, List<ShopcartBO> shopcartList) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        //包邮费用设置为0
        Integer post = 0;


        String orderId = sid.nextShort();

        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);

        //插入订单表
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);
        newOrder.setReceiverName(userAddress.getReceiver());
        newOrder.setReceiverMobile(userAddress.getMobile());
        newOrder.setReceiverAddress(userAddress.getProvince() + " " + userAddress.getCity() + " " + userAddress.getDetail());
        newOrder.setPostAmount(post);
        newOrder.setPayMethod(submitOrderBO.getPayMethod());
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.no.type);
        newOrder.setIsDelete(YesOrNo.no.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(newOrder.getCreatedTime());

        //根据itemSpecIds 保存订单商品关联表

        String[] split = itemSpecIds.split(",");
        Integer totalAmount = 0;
        Integer realPayAmount = 0;
        List<ShopcartBO> toBeRemovedShopCartList = new ArrayList<>();
        for (String s : split) {

            ShopcartBO shopcart = getBuyCountsFromShopcart(shopcartList, s);
            toBeRemovedShopCartList.add(shopcart);

            //定义购买数量为1
            Integer buyCounts = shopcart.getBuyCounts();

            //获取商品规格信息
            ItemsSpec itemsSpec = itemService.queryItemSpecById(s);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount();

            //根据商品ID，获取商品信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items items = itemService.queryItemById(itemId);
            String url = itemService.queryItemMainImgById(itemId);

            //循环保存子订单到数据库
            OrderItems orderItems = new OrderItems();
            String subOrderId = sid.nextShort();
            orderItems.setId(subOrderId);
            orderItems.setOrderId(orderId);
            orderItems.setItemId(itemId);
            orderItems.setItemName(items.getItemName());
            orderItems.setItemImg(url);
            orderItems.setBuyCounts(1);
            orderItems.setItemSpecId(s);
            orderItems.setItemSpecName(itemsSpec.getName());
            orderItems.setPrice(itemsSpec.getPriceDiscount());
            //保存子订单表
            orderItemsMapper.insert(orderItems);

            //用户提交订单后，减少对应的库存
            itemService.decreaseItemSpecStock(s, buyCounts);
        }
        //订单表价格
        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        //保存订单表
        ordersMapper.insert(newOrder);

        //保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        //构建商户订单，传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + post);
        merchantOrdersVO.setPayMethod(payMethod);

        //构建自定义订单VO
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setShopcartList(toBeRemovedShopCartList);

        return orderVO;
    }

    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
       return  orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public void closeOrder() {
        //查询所有未付款订单，判断时间是否超时(1天)，超时则关闭交易
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(queryOrder);
        for (OrderStatus orderStatus : list) {
            //获得订单创建时间
            Date createTime = orderStatus.getCreatedTime();
            //和当前时间进行对比
            int days = DateUtil.daysBetween(createTime, new Date());
            if(days > 1){
                //超过一天关闭订单
                doCloseOrder(orderStatus.getOrderId());
            }
        }
    }

    void doCloseOrder(String orderId){
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(close);
    }

    /**
     * 从redis中购物车获取商品，目的处理counts
     * @param shopcartList
     * @param specId
     * @return
     */
    private ShopcartBO getBuyCountsFromShopcart(List<ShopcartBO> shopcartList,String specId){
        for (ShopcartBO shopcartBO : shopcartList) {
            if(shopcartBO.getSpecId().equals(specId)){
                return shopcartBO;
            }
        }
        return null;

    }
}
