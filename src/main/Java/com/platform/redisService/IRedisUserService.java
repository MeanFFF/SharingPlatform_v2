package com.platform.redisService;

import com.platform.pojo.SignIn;
import com.platform.pojo.User;

public interface IRedisUserService {
    User getUser(Integer userId);

    void setOrUpdateUserCache(User user);

    void updateUserInfoCache(Integer userId);

    void setResetPwdUUid(String uuid, Integer userId);

    Integer getResetPwdUserId(String uuid);

    void delete(String key);

    SignIn getSignInInfo(Integer userId);

    void updateSignInfoCache(Integer userId);
}
