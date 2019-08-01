package com.platform.service;

import com.github.pagehelper.PageInfo;
import com.platform.common.ServerResponse;
import com.platform.pojo.Product;
import com.platform.vo.ProductVo;

public interface IProductService {
    ServerResponse<PageInfo> manageProductList(int pageNum, int pageSize);

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setProductStatus(Integer productId, Integer status);

    ServerResponse getProductDetail(Integer productId);

    ProductVo assembleProductVo(Product product);

    ServerResponse<PageInfo> UserProductList(int pageNum, int pageSize);
}
