package com.platform.dao;

import com.platform.pojo.SignIn;

public interface SignInMapper {
    int insert(SignIn record);

    int insertSelective(SignIn record);

    SignIn selectByUserId(Integer userId);

    int selectDaysByUserId(Integer userId);

    int updateSignIn(Integer userId);
}