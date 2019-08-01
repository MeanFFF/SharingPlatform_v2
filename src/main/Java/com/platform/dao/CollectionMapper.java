package com.platform.dao;

import com.platform.pojo.Collection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionMapper {
    int insert(Collection record);

    int insertSelective(Collection record);

    Collection selectByUserIdFileId(@Param("userId") Integer userId, @Param("fileId") Integer fileId);

    int delByUserIdFileId(@Param("userId") Integer userId, @Param("fileId") Integer fileId);

    List<Collection> selectByUserId(Integer userId);
}