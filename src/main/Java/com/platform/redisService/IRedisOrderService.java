package com.platform.redisService;

import com.platform.pojo.Order;

public interface IRedisOrderService {
    Order getOrder(Long orderNo, Integer userId);

    void saveOrUpdateOrderCache(Order order);
}
