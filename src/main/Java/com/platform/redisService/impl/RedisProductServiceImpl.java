package com.platform.redisService.impl;

import com.platform.dao.ProductMapper;
import com.platform.pojo.Product;
import com.platform.redisService.IRedisProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisProductServiceImpl implements IRedisProductService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private ProductMapper productMapper;

    public Product getProduct(Integer productId){
        Product product = redisCacheUtil.getHashToObjectBykey("product:"+productId, Product.class);
        if(product.getId() == null){
            product = productMapper.selectById(productId);
            if(product != null){
                this.saveOrUpdateProductCache(product);
            }
        }

        return product;
    }

    public void saveOrUpdateProductCache(Product product) {
        redisCacheUtil.delete("product:"+product.getId());
        redisCacheUtil.setObjectToHash("product:"+product.getId(), product);
    }

    /**
     * 更新缓存中商品的状态
     * @param productId
     * @param status
     */
    public void updateProductCacheStatus(Integer productId, Integer status){
        Product product = this.getProduct(productId);
        if(product.getId() != null){
            product.setStatus(status);
            this.saveOrUpdateProductCache(product);
        }
    }



}
