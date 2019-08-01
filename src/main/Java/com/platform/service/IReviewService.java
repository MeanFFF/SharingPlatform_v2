package com.platform.service;

import com.platform.common.ServerResponse;

public interface IReviewService {

    ServerResponse setReviewStatus(Integer reviewId, Boolean status);

}
