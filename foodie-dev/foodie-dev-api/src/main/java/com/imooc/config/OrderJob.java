package com.imooc.config;

import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 使用定时任务关闭超时未支付订单，会存在弊端
     * 1.会有时间差
     *
     * 2.不支持集群
     *      单机没毛病，如果使用集群，就会有多个定时任务
     *      解决方案：只使用一台服务器，单独执行定时任务
     *
     *  3.会对数据库全表搜索，影响数据库性能
     *
     *  //后续涉及到消息队列：MQ->RabbitMQ RocketMQ Kafka ZeroMQ...
     *      使用延时队列
     *      
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoCloseOrder(){
        orderService.closeOrder();
//        System.out.println("执行定时任务，当前时间为"+ DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));
    }
}
