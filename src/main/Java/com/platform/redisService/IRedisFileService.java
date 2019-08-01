package com.platform.redisService;

import com.platform.pojo.File;

public interface IRedisFileService {
    File getFile(Integer fileId);

    void setOrUpdateFileCache(File file);

    void delFileCache(File file);

    void setCollectionCache(Integer userId, Integer fileId);

    void delCollectionCache(Integer userId, Integer fileId);

    String getCollectionCache(Integer userId, Integer fileId);

    void setDownloadCache(Integer userId, Integer fileId);

    String getDownloadCache(Integer userId, Integer fileId);

}
