package com.platform.service;

import com.github.pagehelper.PageInfo;
import com.platform.common.ServerResponse;

import java.util.Map;

public interface IOrderService {
    ServerResponse<PageInfo> getManageList(int pageNum, int pageSize);

    ServerResponse getOrderDetail(Long orderNo);

    ServerResponse createOrder(Integer userId, Integer productId);

    ServerResponse cancelOrder(Integer userId, Long orderNo);

    ServerResponse getOrderList(Integer userId, int pageNum, int pageSize);

    ServerResponse userOrderDetail(Integer userId, Long orderNo);

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse aliCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
