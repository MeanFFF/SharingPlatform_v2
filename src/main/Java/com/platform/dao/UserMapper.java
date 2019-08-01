package com.platform.dao;

import com.platform.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int insert(User record);

    int insertSelective(User record);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    List<User> selectList();

    List<User> selectListOrderByScoreDesc();

    List<User> selectListOrderByResumeScoreDesc();

    List<User> selectListByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    int updateUserByPrimaryKeySelective(User user);

    User selectByPrimaryKey(Integer id);

    Integer getCount();

    int checkEmail(String email);

    User selectByUsername(String username);
}