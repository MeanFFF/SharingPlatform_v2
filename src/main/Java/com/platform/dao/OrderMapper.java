package com.platform.dao;

import com.platform.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectList();

    Order selectByOrderNo(Long orderNo);

    Integer getCount();

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    int updateByPrimaryKeySelective(Order order);

    List<Order> selectByUserId(Integer userId);
}