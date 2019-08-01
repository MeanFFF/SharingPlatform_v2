package com.platform.dao;

import com.platform.pojo.PayInfo;

public interface PayInfoMapper {
    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    PayInfo selectByOrderNo(Long orderNo);

    void updatePayInfoByPrimaryKeySelective(PayInfo payInfo);
}