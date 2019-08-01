package com.platform.redisService;

import com.platform.pojo.Product;

public interface IRedisProductService {
    Product getProduct(Integer productId);

    void saveOrUpdateProductCache(Product product);

    void updateProductCacheStatus(Integer productId, Integer status);
}
