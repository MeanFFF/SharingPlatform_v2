package com.platform.dao;

import com.platform.pojo.Product;

import java.util.List;

public interface ProductMapper {
    int insert(Product record);

    int insertSelective(Product record);

    Product selectById(Integer productId);

    List<Product> selectList();

    int updateByPrimaryKeySelective(Product product);

    List<Product> selectListByStatus(String status);
}