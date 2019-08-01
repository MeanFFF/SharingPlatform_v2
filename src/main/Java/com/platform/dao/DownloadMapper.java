package com.platform.dao;

import com.platform.pojo.Download;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadMapper {
    int insert(Download record);

    int insertSelective(Download record);

    Download selectByUserIdFileId(@Param("userId") Integer userId, @Param("fileId") Integer fileId);

    List<Download> selectByUserId(Integer userId);
}