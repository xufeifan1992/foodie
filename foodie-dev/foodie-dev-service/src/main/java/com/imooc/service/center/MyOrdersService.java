package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.OrderStatusCountsVO;
import com.imooc.utils.PagedGridResult;

public interface MyOrdersService {

    /**
     * 查询我的订单了列表
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId,Integer orderStatus,Integer page, Integer pageSize);

    /**
     * 订单状态->商家发货
     * @param orderId
     */
    void updateDeliverOrderStatus(String orderId);

    /**
     * 查询我的订单
     * @param userId
     * @param orderId
     * @return
     */
    Orders queryMyOrder(String orderId, String userId);

    /**
     * 更新订单状态确认收货
     * @param orderId
     * @return
     */
    boolean updateReceiveOrderStatus(String orderId);

    /**
     * 逻辑删除订单状态
     * @param userId
     * @param orderId
     * @return
     */
    boolean deleteOrder(String userId,String orderId);

    /**
     * 查询用户订单数
     * @param userId
     */
    public OrderStatusCountsVO getOrderStatusCounts(String userId);

    /**
     * 获取分页订单动向
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize);
}
