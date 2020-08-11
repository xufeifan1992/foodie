package com.imooc.service.center.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.*;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.MyCommentVO;
import com.imooc.pojo.vo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentService;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.krb5.internal.PAData;

import javax.print.attribute.standard.PageRanges;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MyCommentServiceImpl extends BaseService implements MyCommentService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);

        return orderItemsMapper.select(query);
    }

    @Override
    public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> list) {
        //保存评价item_comments
        for (OrderItemsCommentBO oic : list) {
            oic.setCommentId(sid.nextShort());
        }
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        paramsMap.put("commentList",list);
        itemsCommentsMapperCustom.saveComments(paramsMap);


        //修改订单表改为已评价orders
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.yes.type);
        ordersMapper.updateByPrimaryKeySelective(orders);

        //订单状态表 order_status
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);

    }

    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(paramsMap);
        PagedGridResult pagedGridResult = setterPagedGrid(page, list);
        return pagedGridResult;
    }


}
