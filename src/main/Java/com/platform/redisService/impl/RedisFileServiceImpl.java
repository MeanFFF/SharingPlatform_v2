package com.platform.redisService.impl;

import com.platform.dao.CollectionMapper;
import com.platform.dao.DownloadMapper;
import com.platform.dao.FileMapper;
import com.platform.pojo.Collection;
import com.platform.pojo.Download;
import com.platform.pojo.File;
import com.platform.redisService.IRedisFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisFileServiceImpl implements IRedisFileService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private DownloadMapper downloadMapper;

    public File getFile(Integer fileId) {
        File file = redisCacheUtil.getHashToObjectBykey("file:" + fileId, File.class);
        // 会创建一个file的空对象, 不能判断file为null
        if (file.getId() == null) {
            file = fileMapper.selectByPrimaryKey(fileId);
            if(file != null){
                this.setFileCache(file);
            }
        }
        // 返回的file可能为空
        return file;
    }

    public void setFileCache(File file) {
        redisCacheUtil.delete("file:" + file.getId());
        redisCacheUtil.setObjectToHash("file:" + file.getId(), file);
    }

    public void DelFileCache(File file) {
        redisCacheUtil.delete("file:" + file.getId());
    }

    public void setOrUpdateFileCache(File file) {
        redisCacheUtil.delete("file:" + file.getId());
        redisCacheUtil.setObjectToHash("file:" + file.getId(), file);
    }

    public void delFileCache(File file) {
        redisCacheUtil.delete("file:" + file.getId());
    }


    public void setCollectionCache(Integer userId, Integer fileId) {
        redisCacheUtil.set("clec:" + userId + ":" + fileId, "true");
    }

    public void delCollectionCache(Integer userId, Integer fileId) {
        redisCacheUtil.delete("clec:" + userId + ":" + fileId);
    }

    public String getCollectionCache(Integer userId, Integer fileId){
        String str = (String) redisCacheUtil.get("clec:" + userId + ":" + fileId);
        if(str == null || !str.equals("true")){
            Collection collection = collectionMapper.selectByUserIdFileId(userId, fileId);
            if(collection != null){
                setCollectionCache(userId, fileId);
                str = "true";
            }
        }
        return str;
    }

    public void setDownloadCache(Integer userId, Integer fileId) {
        redisCacheUtil.set("down:" + userId + ":" + fileId, "true");
    }


    public String getDownloadCache(Integer userId, Integer fileId){
        String str = (String) redisCacheUtil.get("down:" + userId + ":" + fileId);
        if(str == null || !str.equals("true")){
            Download download = downloadMapper.selectByUserIdFileId(userId, fileId);
            if(download != null){
                setDownloadCache(userId, fileId);
                str = "true";
            }
        }
        return str;
    }



}
