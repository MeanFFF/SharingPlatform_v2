package com.platform.service;

import com.platform.common.ServerResponse;

public interface ISignInService {

    ServerResponse signIn(Integer userId);

    ServerResponse getSignInStatus(Integer userId);
}
