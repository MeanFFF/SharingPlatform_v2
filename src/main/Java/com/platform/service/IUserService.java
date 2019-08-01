package com.platform.service;

import com.github.pagehelper.PageInfo;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;


public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse loginSendCode(User currentUser);

    ServerResponse resetPassword(String uuid, String newPwd);

    ServerResponse getUserDetail(Integer userId);

    ServerResponse<User> getInformation(Integer id);

    ServerResponse activateUser(String uuid);

    ServerResponse checkUUid(String uuid);

    ServerResponse comment(Integer userId, Integer fileId, String content);



    ServerResponse checkAdminRole(User user);

    ServerResponse<PageInfo> getList(int pageNum, int pageSize, Integer tag);

    ServerResponse<PageInfo> getSearchList(String username, String email, int pageNum, int pageSize, Integer tag);

    ServerResponse setUserStatus(Integer userId, Integer status);

    ServerResponse saveOrUpdateUser(User user);

    ServerResponse getBaseCount();

}
