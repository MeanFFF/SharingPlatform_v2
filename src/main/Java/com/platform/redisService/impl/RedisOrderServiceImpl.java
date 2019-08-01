package com.platform.redisService.impl;

import com.platform.dao.OrderMapper;
import com.platform.pojo.Order;
import com.platform.redisService.IRedisOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RedisOrderServiceImpl implements IRedisOrderService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private OrderMapper orderMapper;


    public Order getOrder(Long orderNo, Integer userId){
        // 判断缓存中有没有该订单
        Order order = redisCacheUtil.getHashToObjectBykey("order:"+orderNo, Order.class);
        if(order.getOrderNo() == null){
            // 缓存中没有, 从数据库中获取
            order = orderMapper.selectByOrderNo(orderNo);
            // 数据库中有, 则存入缓存中, 没有则返回空
            if(order != null){
                // 存入缓存中
                this.saveOrUpdateOrderCache(order);
            }
        }

        // 判断是否给出userId, 该订单是否为空
        if(userId != null && order != null){
            // 有userId, 判断它与订单的userId是否相同, 如果不相同, 订单为空
            // 这个订单不是这个用户的
            if(!order.getUserId().equals(userId)){
                order = null;
            }
        }

        return order;
    }

    public void saveOrUpdateOrderCache(Order order) {
        redisCacheUtil.delete("order:" + order.getOrderNo());
        redisCacheUtil.setObjectToHash("order:" + order.getOrderNo(), order);
    }




}
