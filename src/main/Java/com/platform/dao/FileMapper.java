package com.platform.dao;

import com.platform.pojo.File;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper {
    int insert(File record);

    int insertSelective(File record);

    List<File> selectSearchByIdAndName(@Param("fileId") Integer fileId, @Param("fileName") String fileName);

    List<File> selectAllFile();

    List<File> selectFileByStatus(int status);

    List<File> selectFileOrderByTimesDesc();

    int updateByPrimaryKeySelective(File file);

    Integer getCount();

    File selectByPrimaryKey(Integer fileId);

    List<File> selectByUploadUserId(Integer userId);

    List<File> selectByNameAndCategoryIds(@Param("keyword") String keyword, @Param("categoryIdList") List<Integer> categoryIdList);
}