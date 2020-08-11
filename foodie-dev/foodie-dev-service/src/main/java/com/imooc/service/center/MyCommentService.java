package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.vo.center.OrderItemsCommentBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentService {

    /**
     * 根据订单ID查询订单商品
     * @param orderId
     * @return
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存评价
     * @param orderId
     * @param userId
     * @param list
     */
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> list);

    /**
     * 查询评价
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);
}
