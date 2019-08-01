package com.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.dao.ProductMapper;
import com.platform.pojo.Category;
import com.platform.pojo.Product;
import com.platform.redisService.IRedisProductService;
import com.platform.service.IProductService;
import com.platform.vo.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private IRedisProductService iRedisProductService;

    /**
     * 管理员获得产品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> manageProductList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);

        List<Product> productList = productMapper.selectList();

        List<ProductVo> productVoList = assembleProductVoList(productList);

        PageInfo pageResult = new PageInfo(productList);

        pageResult.setList(productVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 用户获得商品信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> UserProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Product> productList = productMapper.selectListByStatus(Const.ProductStatusEnum.ON_LINE.getValue());

        List<ProductVo> productVoList = assembleProductVoList(productList);

        PageInfo pageResult = new PageInfo(productList);

        pageResult.setList(productVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 添加商品或更新商品信息
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            product.setUpdateTime(new Date());
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0){
                    // 更新插入缓存
                    iRedisProductService.saveOrUpdateProductCache(product);
                    return ServerResponse.createBySuccess("更新商品信息成功");
                }
                return ServerResponse.createBySuccess("更新商品信息失败");
            }else{
                product.setCreateTime(product.getUpdateTime());
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccess("添加商品成功");
                }
                return ServerResponse.createBySuccess("添加商品失败");
            }
        }

        return ServerResponse.createByErrorMessage("添加或更新商品参数不正确");
    }

    /**
     * 设置商品状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse setProductStatus(Integer productId, Integer status){
        if(productId == null || Const.ProductStatusEnum.codeOf(status) == null){
            return ServerResponse.createByErrorMessage("设置商品状态参数错误");
        }
        iRedisProductService.updateProductCacheStatus(productId, status);
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        return saveOrUpdateProduct(product);
    }

    @Override
    public ServerResponse getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = iRedisProductService.getProduct(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }

        return ServerResponse.createBySuccess(product);
    }


    private List<ProductVo> assembleProductVoList(List<Product> productList){
        List<ProductVo> productVoList = Lists.newArrayList();

        for(Product productItem : productList){
            ProductVo productVo = assembleProductVo(productItem);
            productVoList.add(productVo);
        }

        return productVoList;

    }

    /**
     * 用户获得产品列表时
     * 判断产品状态
     * 要封装, 有一些东西拿不到
     */
    public ProductVo assembleProductVo(Product product){
        ProductVo productVo = new ProductVo();
        productVo.setId(product.getId());
        productVo.setCategory(product.getCategory());
        productVo.setCategoryDesc(Const.ProductCategoryEnum.codeOf(product.getCategory()).getValue());
        productVo.setName(product.getName());
        productVo.setPrice(product.getPrice());
        productVo.setStatus(product.getStatus());
        if(product.getStatus() != null){
            productVo.setStatusDesc(Const.ProductStatusEnum.codeOf(product.getStatus()).getValue());
        }
        productVo.setDetail(product.getDetail());
        return productVo;
    }



}
