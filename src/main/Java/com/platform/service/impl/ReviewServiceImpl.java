package com.platform.service.impl;

import com.platform.common.ServerResponse;
import com.platform.dao.ReviewMapper;
import com.platform.pojo.Review;
import com.platform.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public ServerResponse setReviewStatus(Integer reviewId, Boolean status){
        Review review = new Review();
        review.setId(reviewId);
        review.setStatus(status);
        int rowCount = reviewMapper.updateByPrimaryKeySelective(review);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("设置评论状态成功");
        }
        return ServerResponse.createByErrorMessage("设置评论状态失败");

    }

}
