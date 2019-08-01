package com.platform.dao;

import com.platform.pojo.Review;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReviewMapper {
    int insert(Review record);

    int insertSelective(Review record);

    List<Review> selectByFileId(Integer fileId);

    int updateByPrimaryKeySelective(Review review);

    int selectCountByFileId(Integer fileId);

    List<Review> selectByFileIdAndStatus(@Param("fileId") Integer fileId, @Param("status") int status);
}