package com.imooc.service.center.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.mapper.OrdersMapperCustom;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MyOrdersServiceImpl extends BaseService implements MyOrdersService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (orderStatus != null) {
            map.put("orderStatus", orderStatus);
        }

        List<MyOrdersVO> list = ordersMapperCustom.queryMyOrders(map);
        PageHelper.startPage(page, pageSize);

        return setterPagedGrid(page, list);
    }

    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        updateOrder.setDeliverTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);


        orderStatusMapper.updateByExampleSelective(updateOrder, example);


    }

    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    @Override
    public Orders queryMyOrder(String orderId, String userId) {
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setIsDelete(YesOrNo.no.type);

        Orders one = ordersMapper.selectOne(orders);

        return one;
    }

    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        orderStatus.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);

        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);

        return result == 1 ? true : false;
    }

    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setIsDelete(YesOrNo.yes.type);
        orders.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",orderId);
        criteria.andEqualTo("userId", userId);

        int result = ordersMapper.updateByExampleSelective(orders, example);

        return result == 1 ? true : false;
    }

    @Override
    public OrderStatusCountsVO getOrderStatusCounts(String userId) {
        //待付款数量
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        int waitPayCount = ordersMapperCustom.getMyOrderStatusCounts(map);

        //已付款待发货
        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCount = ordersMapperCustom.getMyOrderStatusCounts(map);

        //已发货待收货
        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCount = ordersMapperCustom.getMyOrderStatusCounts(map);

        //待评价
        map.put("orderStatus", OrderStatusEnum.SUCCESS);
        map.put("isComment", YesOrNo.no.type);
        int waitCommentCount = ordersMapperCustom.getMyOrderStatusCounts(map);

        OrderStatusCountsVO countsVO = new OrderStatusCountsVO(waitPayCount, waitDeliverCount, waitReceiveCount, waitCommentCount);

        return countsVO;

    }

    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<OrderStatus> myOrderTrend = ordersMapperCustom.getMyOrderTrend(map);
        PagedGridResult pagedGridResult = setterPagedGrid(page, myOrderTrend);
        return pagedGridResult;
    }


}
