package com.platform.controller.portal;

import com.github.pagehelper.PageInfo;
import com.platform.common.ServerResponse;
import com.platform.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @RequestMapping("list")
    @ResponseBody
    public ServerResponse<PageInfo> UserProductList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return iProductService.UserProductList(pageNum, pageSize);
    }

}
