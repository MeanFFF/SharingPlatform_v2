package com.platform.service;

import com.github.pagehelper.PageInfo;
import com.platform.common.ServerResponse;
import com.platform.pojo.File;
import org.springframework.web.multipart.MultipartFile;


public interface IFileService {
    ServerResponse<PageInfo> getFileList(int pageNum, int pageSize);

    ServerResponse getSearchFileList(Integer fileId, String fileName, int pageNum, int pageSize, int tag);

    ServerResponse setFileStatus(Integer fileId, Integer status);

    ServerResponse getManegeFileDetail(Integer fileId);

    String uploadFile(MultipartFile file, String path);

    ServerResponse addFile(Integer userId, File file);

    ServerResponse<PageInfo> getUncheckedFileList(int pageNum, int pageSize);

    ServerResponse<PageInfo> getFileListOrderTimesDesc(int pageNum, int pageSize);

    ServerResponse saveOrUpdateFile(File file);

    ServerResponse collectFile(Integer userId, Integer fileId);

    ServerResponse unCollectFile(Integer userId, Integer fileId);

    ServerResponse getCollectionList(Integer userId, int pageNum, int pageSize);

    ServerResponse downloadFile(Integer userId, Integer fileId);

    ServerResponse<PageInfo> getUploadList(Integer userId, int pageNum, int pageSize);

    ServerResponse getDownloadList(Integer userId, int pageNum, int pageSize);

    ServerResponse getFileDetail(int fileId);

    ServerResponse getCollectStatus(int userId, int fileId);

    ServerResponse<PageInfo> getFileByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
