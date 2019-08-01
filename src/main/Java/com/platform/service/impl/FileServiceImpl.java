package com.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.dao.*;
import com.platform.pojo.*;
import com.platform.pojo.Collection;
import com.platform.redisService.IRedisFileService;
import com.platform.redisService.IRedisUserService;
import com.platform.redisService.impl.RedisCacheUtil;
import com.platform.service.ICategoryService;
import com.platform.service.IFileService;
import com.platform.utils.FTPUtil;
import com.platform.utils.PropertiesUtil;
import com.platform.vo.FileDetailVo;
import com.platform.vo.FileListVo;
import com.platform.vo.FileManageDetailVo;
import com.platform.vo.ReviewVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CollectionMapper collectionMapper;
    @Autowired
    private DownloadMapper downloadMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Autowired
    private IRedisFileService iRedisFileService;
    @Autowired
    private IRedisUserService iRedisUserService;


    // 获取文件列表
    @Override
    public ServerResponse<PageInfo> getFileList(int pageNum, int pageSize) {
        return getSearchFileList(null, null, pageNum, pageSize, Const.SearchFileTag.NORMAL);

    }

    /**
     * 管理员
     * 获取未审核的文件列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getUncheckedFileList(int pageNum, int pageSize) {
        return getSearchFileList(null, null, pageNum, pageSize, Const.SearchFileTag.GET_UNCHECKED);
    }

    /**
     * 获取按照下载次数倒序文件列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getFileListOrderTimesDesc(int pageNum, int pageSize) {
        return getSearchFileList(null, null, pageNum, pageSize, Const.SearchFileTag.ORDER_BY_TIMES);
    }

    /**
     * 添加或更新文件
     * fileId为空时:   添加文件
     * 不为空时: 更新文件信息
     *
     * @param file
     * @return
     */
    @Override
    public ServerResponse saveOrUpdateFile(File file) {
        if (file != null) {
            file.setUpdateTime(new Date());
            if (file.getId() != null) {
                int rowCount = fileMapper.updateByPrimaryKeySelective(file);
                if (rowCount > 0) {
                    iRedisFileService.setOrUpdateFileCache(file);
                    return ServerResponse.createBySuccess("更新文件信息成功");
                }
                return ServerResponse.createBySuccess("更新文件信息失败");
            } else {
                file.setCreateTime(file.getUpdateTime());
                int rowCount = fileMapper.insert(file);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("添加文件成功");
                }
                return ServerResponse.createBySuccess("添加文件失败");

            }

        }
        return ServerResponse.createByErrorMessage("添加或更新文件参数不正确");
    }

    @Override
    public ServerResponse collectFile(Integer userId, Integer fileId) {
        if (fileId != null) {

            String str = iRedisFileService.getCollectionCache(userId, fileId);

            if (str != null) {
                return ServerResponse.createByErrorMessage("你已经收藏过这个文件了");
            } else {
                Collection newCollection = new Collection();
                newCollection.setUserId(userId);
                newCollection.setFileId(fileId);
                newCollection.setCreateTime(new Date());
                newCollection.setUpdateTime(newCollection.getCreateTime());
                int row = collectionMapper.insertSelective(newCollection);
                if (row > 0) {
//                    cache.put("cost:" + userId + ":" + fileId, true);
                    iRedisFileService.setCollectionCache(userId, fileId);
                    return ServerResponse.createBySuccess("收藏文件成功");
                } else {
                    return ServerResponse.createByErrorMessage("收藏文件失败");
                }
            }

        }
        return ServerResponse.createByErrorMessage("收藏文件错误");
    }

    @Override
    public ServerResponse unCollectFile(Integer userId, Integer fileId) {
        if (fileId != null) {
            String str = iRedisFileService.getCollectionCache(userId, fileId);
            if (str != null) {
                int row = collectionMapper.delByUserIdFileId(userId, fileId);
                if (row > 0) {
                    iRedisFileService.delCollectionCache(userId, fileId);
                    return ServerResponse.createBySuccess("取消收藏成功");
                } else {
                    return ServerResponse.createByErrorMessage("取消收藏失败");
                }

            } else {
                return ServerResponse.createByErrorMessage("你还没有收藏这个文件");
            }

        }
        return ServerResponse.createByErrorMessage("取消收藏错误");
    }

    /**
     * 封装 文件 todo
     *
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getCollectionList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Collection> collectionList = collectionMapper.selectByUserId(userId);
        List<FileListVo> fileListVoList = Lists.newArrayList();
        for (Collection collection : collectionList) {
            File file = fileMapper.selectByPrimaryKey(collection.getFileId());
            FileListVo fileListVo = assembleFileListVo(file);
            fileListVoList.add(fileListVo);
        }
        PageInfo result = new PageInfo(collectionList);
        result.setList(fileListVoList);
        return ServerResponse.createBySuccess(result);
    }

    // todo
    @Override
    public ServerResponse<PageInfo> getUploadList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<File> fileList = fileMapper.selectByUploadUserId(userId);
        List<FileListVo> fileListVoList = assembleFileListVoList(fileList);
        PageInfo result = new PageInfo(fileList);
        result.setList(fileListVoList);
        return ServerResponse.createBySuccess(result);
    }

    // todo
    @Override
    public ServerResponse getDownloadList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Download> downloadList = downloadMapper.selectByUserId(userId);
        List<FileListVo> fileListVoList = Lists.newArrayList();
        for (Download download : downloadList) {
            File file = fileMapper.selectByPrimaryKey(download.getFileId());
            FileListVo fileListVo = assembleFileListVo(file);
            fileListVoList.add(fileListVo);
        }
        PageInfo result = new PageInfo(downloadList);
        result.setList(fileListVoList);
        return ServerResponse.createBySuccess(result);
    }

    @Override
    public ServerResponse getFileDetail(int fileId) {
        File file = iRedisFileService.getFile(fileId);
        if (file == null) {
            return ServerResponse.createByErrorMessage("获取文件信息错误");
        }
        FileDetailVo fileDetailVo = assembleFileDetailVo(file);
        return ServerResponse.createBySuccess(fileDetailVo);
    }

    @Override
    public ServerResponse getCollectStatus(int userId, int fileId) {

        String str = iRedisFileService.getCollectionCache(userId, fileId);
        if (str != null) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByError();
        }

    }

    @Override
    public ServerResponse<PageInfo> getFileByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = new ArrayList<>();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            // categoryId在数据库中不存在, 但是是所有品类的根节点, 所以要特殊判断
            // categoryId为0时,显示所有文件
            if (categoryId != 0 && category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<FileListVo> fileListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(fileListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }

        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.FileListOrderBy.FILE_ORDERBY.contains(orderBy)) {
                String[] orderByArray = orderBy.split("-");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }

        List<File> fileList = fileMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);

        List<FileListVo> fileListVoList = assembleFileListVoList(fileList);
        PageInfo pageResult = new PageInfo(fileList);
        pageResult.setList(fileListVoList);
        return ServerResponse.createBySuccess(pageResult);


    }

    @Override
    public ServerResponse downloadFile(Integer userId, Integer fileId) {

        String str = iRedisFileService.getDownloadCache(userId, fileId);
        File file = iRedisFileService.getFile(fileId);
        // 还没有下载过该文件
        if (str == null) {
            User user = iRedisUserService.getUser(userId);
            if (user == null || file == null) {
                return ServerResponse.createByErrorMessage("获取当前用户/文件失败");
            }
            if (user.getTotalScore() >= file.getScore()) {
                // 插入下载信息
                Download newDownload = new Download();
                newDownload.setUserId(userId);
                newDownload.setFileId(fileId);
                newDownload.setCreateTime(new Date());
                newDownload.setUpdateTime(new Date());
                User uploadFileUser = iRedisUserService.getUser(file.getUploadUserId());
                uploadFileUser.setTotalScore(uploadFileUser.getTotalScore() + file.getScore());
                int row1 = downloadMapper.insert(newDownload);
                if (row1 > 0) {
                    iRedisFileService.setDownloadCache(userId, fileId);
                    user.setTotalScore(user.getTotalScore() - file.getScore());
                    user.setResumeScore(user.getResumeScore() + file.getScore());
                    int row2 = userMapper.updateUserByPrimaryKeySelective(user);
                    if (row2 > 0) {
                        iRedisUserService.updateUserInfoCache(userId);
                        file.setTimes(file.getTimes() + 1);
                        int row3 = fileMapper.updateByPrimaryKeySelective(file);
                        if (row3 > 0) {
                            userMapper.updateUserByPrimaryKeySelective(uploadFileUser);
                            iRedisUserService.updateUserInfoCache(uploadFileUser.getId());
                            iRedisFileService.setOrUpdateFileCache(file);
                        }else{
                            return ServerResponse.createByErrorMessage("下载失败");
                        }
                    }else{
                        return ServerResponse.createByErrorMessage("下载失败，请重新下载");
                    }
                }
            } else {
                return ServerResponse.createByErrorMessage("积分不够，不能下载");
            }

        }

        Map map = Maps.newHashMap();
        map.put("fileName", file.getName());
        map.put("fileAddress", file.getAddress());
        return ServerResponse.createBySuccess(map);
    }


//    @Override
//    public ServerResponse downloadFile(Integer userId, Integer fileId) {
//
//        String str = iRedisFileService.getDownloadCache(userId, fileId);
//        File file = fileMapper.selectByPrimaryKey(fileId);
//        if (str != null) {
////            boolean flag = download(fileId);
////            if (flag) {
////                return ServerResponse.createBySuccess("下载成功，请到文件夹中查看");
////            }
////            return ServerResponse.createByErrorMessage("文件下载失败，请重新下载");
//            Map map = Maps.newHashMap();
//            map.put("fileName",file.getName());
//            map.put("fileAddress", file.getAddress());
//            return ServerResponse.createBySuccess(map);
//        } else {
//            User user = userMapper.selectByPrimaryKey(userId);
////            File file = fileMapper.selectByPrimaryKey(fileId);
//            if (user == null || file == null) {
//                return ServerResponse.createByErrorMessage("获取当前用户/文件失败");
//            }
//            if (user.getTotalScore() >= file.getScore()) {
//                Download newDownload = new Download();
//                newDownload.setUserId(userId);
//                newDownload.setFileId(fileId);
//                newDownload.setCreateTime(new Date());
//                newDownload.setUpdateTime(new Date());
//                int row1 = downloadMapper.insert(newDownload);
//                if (row1 > 0) {
//                    iRedisFileService.setDownloadCache(userId, fileId);
//                    user.setTotalScore(user.getTotalScore() - file.getScore());
//                    user.setResumeScore(user.getResumeScore() + file.getScore());
//                    int row2 = userMapper.updateUserByPrimaryKeySelective(user);
//                    if (row2 > 0) {
//                        file.setTimes(file.getTimes() + 1);
//                        int row3 = fileMapper.updateByPrimaryKeySelective(file);
//                        if (row3 > 0) {
////                            boolean flag = download(fileId);
////                            if (flag) {
////                                return ServerResponse.createBySuccess("下载成功，请到文件夹中查看");
////                            }
////                            return ServerResponse.createByErrorMessage("文件下载失败，请重新下载");
//                            Map map = Maps.newHashMap();
//                            map.put("fileName",file.getName());
//                            map.put("fileAddress", file.getAddress());
//                            return ServerResponse.createBySuccess(map);
//                        }
//                    }
//                }
//
//            } else {
//                return ServerResponse.createByErrorMessage("积分不够，不能下载");
//            }
//        }
//        return ServerResponse.createByErrorMessage("下载失败，请重新下载");
//    }


    private boolean download(Integer fileId) {
        try {
            String localpath = "G:/ftpdownload/";
            /**
             * 先根据fileId从redis缓存中去文件地址
             * 如果redis中不存在,再从数据库中取
             */
//            File thisFile = cache.get("file:" + fileId, File.class);
//            if (thisFile == null) {
//                thisFile = fileMapper.selectByPrimaryKey(fileId);
//                if (thisFile != null) {
//                    cache.put("file:" + fileId, thisFile);
//                } else {
//                    return false;
//                }
//            }

//            File thisFile = fileMapper.selectByPrimaryKey(fileId);

            File thisFile = iRedisFileService.getFile(fileId);
            if (thisFile == null) {
                return false;
            }
            String filename = thisFile.getName();
            String fileAddress = thisFile.getAddress();

            /**
             * 数据库中保存的地址格式：
             * ftp://172.26.93.207/be9325b8-1f8b-4603-9137-19939e635302.exe
             *
             * 需要获得的字段：
             * be9325b8-1f8b-4603-9137-19939e635302.exe
             */
            fileAddress = fileAddress.substring(fileAddress.lastIndexOf("/") + 1);
            java.io.File file = new java.io.File(localpath + filename);
            if (file.exists()) {
                return true;
            } else {
                file.getParentFile().mkdirs();
                boolean dFlag = FTPUtil.downloadFile(filename, fileAddress, localpath);
                if (dFlag) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取搜索的文件列表
     * 搜索分为用id搜索,用文件名搜索
     *
     * @param fileId
     * @param fileName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse getSearchFileList(Integer fileId, String fileName, int pageNum, int pageSize, int tag) {
        PageHelper.startPage(pageNum, pageSize);


        List<File> fileList;
        if (fileId == null && StringUtils.isBlank(fileName)) {
            switch (tag) {
                case Const.SearchFileTag.NORMAL:
                    fileList = fileMapper.selectAllFile();
                    break;
                case Const.SearchFileTag.GET_UNCHECKED:
                    fileList = fileMapper.selectFileByStatus(Const.FileStatusEnum.UNCHECKED.getCode());
                    break;
                case Const.SearchFileTag.ORDER_BY_TIMES:
                    fileList = fileMapper.selectFileOrderByTimesDesc();
                    break;
                default:
                    return ServerResponse.createByErrorMessage("获取文件列表错误");
            }

        } else {
            if (StringUtils.isNoneBlank(fileName)) {
                fileName = new StringBuilder().append("%").append(fileName).append("%").toString();
            }
            fileList = fileMapper.selectSearchByIdAndName(fileId, fileName);
        }

        List<FileListVo> fileListVoList = assembleFileListVoList(fileList);

        PageInfo pageResult = new PageInfo(fileList);
        pageResult.setList(fileListVoList);
        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     * 管理员
     * 设置文件状态
     * 0:未审核,管理员进行审核
     * 1:前台可以显示
     * 2:"删除"
     *
     * @param fileId 文件id
     * @param status 文件状态
     * @return
     */
    @Override
    public ServerResponse setFileStatus(Integer fileId, Integer status) {
        File file = new File();
        file.setId(fileId);
        file.setStatus(status);
        file.setUpdateTime(new Date());
        int rowCount = fileMapper.updateByPrimaryKeySelective(file);
        if (rowCount > 0) {
            iRedisFileService.delFileCache(file);
            return ServerResponse.createBySuccess("设置文件状态成功");
        }
        return ServerResponse.createByErrorMessage("设置文件状态失败");
    }

    /**
     * 管理员
     * 获取文件的详细信息
     * <p>
     * 用户获取文件详情时, 要判断文件的状态
     * 要重新包装过文件
     *
     * @param fileId 文件id
     * @return
     */
    public ServerResponse getManegeFileDetail(Integer fileId) {
        File file = iRedisFileService.getFile(fileId);
        if (file == null) {
            return ServerResponse.createByErrorMessage("获取文件信息错误");
        }

        FileManageDetailVo fileManageDetailVo = assembleFileManageDetailVo(file);
        return ServerResponse.createBySuccess(fileManageDetailVo);
    }

    /**
     * 包装File
     * File => FileVo
     * 这个FileVo是传给列表的
     * 传给Detail的file就不进行包装了,直接传file
     *
     * @param file file的pojo
     * @return
     */
    public FileListVo assembleFileListVo(File file) {
        FileListVo fileListVo = new FileListVo();
        fileListVo.setId(file.getId());
        fileListVo.setName(file.getName());
        fileListVo.setScore(file.getScore());
        fileListVo.setTimes(file.getTimes());
        fileListVo.setStatus(file.getStatus());
        fileListVo.setStatusDesc(Const.FileStatusEnum.codeOf(file.getStatus()).getValue());
        int reviewCounts = reviewMapper.selectCountByFileId(file.getId());
        fileListVo.setReviewCounts(reviewCounts);
        fileListVo.setCreateTime(file.getCreateTime());
        return fileListVo;
    }

    public List<FileListVo> assembleFileListVoList(List<File> fileList) {
        List<FileListVo> fileListVoList = Lists.newArrayList();
        for (File file : fileList) {
            FileListVo fileListVo = assembleFileListVo(file);
            fileListVoList.add(fileListVo);
        }
        return fileListVoList;
    }

    /**
     * 管理员
     * 包装管理员能看见的文件详细信息, 附带该文件的评论
     *
     * @param file
     * @return
     */
    public FileManageDetailVo assembleFileManageDetailVo(File file) {
        List<Review> reviews = reviewMapper.selectByFileId(file.getId());

        List<ReviewVo> reviewVos = assembleReviewVoList(reviews);

        FileManageDetailVo fileManageDetailVo = new FileManageDetailVo();

        fileManageDetailVo.setId(file.getId());
        fileManageDetailVo.setName(file.getName());
        fileManageDetailVo.setCategoryId(file.getCategoryId());

        Category category = categoryMapper.selectByPrimaryKey(file.getCategoryId());
        if (category == null) {
            fileManageDetailVo.setParentCategoryId(0);
        } else {
            fileManageDetailVo.setParentCategoryId(category.getParentId());
        }

        fileManageDetailVo.setUploadUserId(file.getUploadUserId());
        fileManageDetailVo.setUploadUsername(userMapper.selectByPrimaryKey(file.getUploadUserId()).getUsername());
        fileManageDetailVo.setAddress(file.getAddress());
        fileManageDetailVo.setDetail(file.getDetail());
        fileManageDetailVo.setScore(file.getScore());
        fileManageDetailVo.setTimes(file.getTimes());
        fileManageDetailVo.setStatus(file.getStatus());
        fileManageDetailVo.setCreateTime(file.getCreateTime());
        fileManageDetailVo.setUpdateTime(file.getUpdateTime());
        fileManageDetailVo.setReviewVos(reviewVos);

        return fileManageDetailVo;
    }

    private FileDetailVo assembleFileDetailVo(File file) {
        List<Review> reviews = reviewMapper.selectByFileIdAndStatus(file.getId(), Const.ReviewStatusEnum.SHOW.getCode());

        List<ReviewVo> reviewVos = assembleReviewVoList(reviews);
        FileDetailVo fileDetailVo = new FileDetailVo();

        fileDetailVo.setId(file.getId());
        fileDetailVo.setName(file.getName());
        fileDetailVo.setStatus(file.getStatus());
        fileDetailVo.setCategoryId(file.getCategoryId());
        Category category = categoryMapper.selectByPrimaryKey(file.getCategoryId());
        fileDetailVo.setCategoryDesc(category.getName());
        fileDetailVo.setParentCategoryId(category.getParentId());

        Category parentCategory = categoryMapper.selectByPrimaryKey(fileDetailVo.getParentCategoryId());
        if (parentCategory == null) {
            fileDetailVo.setParentCategoryDesc(null);
        } else {
            fileDetailVo.setParentCategoryDesc(parentCategory.getName());
        }


        fileDetailVo.setUploadUserId(file.getUploadUserId());
        fileDetailVo.setUploadUsername(userMapper.selectByPrimaryKey(file.getUploadUserId()).getUsername());
        fileDetailVo.setDetail(file.getDetail());
        fileDetailVo.setScore(file.getScore());
        fileDetailVo.setTimes(file.getTimes());
        fileDetailVo.setCreateTime(file.getCreateTime());
        fileDetailVo.setUpdateTime(file.getUpdateTime());
        fileDetailVo.setReviewVos(reviewVos);

        return fileDetailVo;
    }

    public List<ReviewVo> assembleReviewVoList(List<Review> reviews) {
        List<ReviewVo> reviewVoList = Lists.newArrayList();

        for (Review review : reviews) {
            ReviewVo reviewVo = assembleReviewVo(review);
            reviewVoList.add(reviewVo);
        }

        return reviewVoList;
    }

    private ReviewVo assembleReviewVo(Review review) {
        ReviewVo reviewVo = new ReviewVo();
        reviewVo.setId(review.getId());
        reviewVo.setUserId(review.getUserId());
        reviewVo.setUsername(userMapper.selectByPrimaryKey(review.getUserId()).getUsername());
        reviewVo.setFileId(review.getFileId());
        reviewVo.setStatus(review.getStatus());
        reviewVo.setContent(review.getContent());
        reviewVo.setCreateTime(review.getCreateTime());
        return reviewVo;
    }


    /**
     * 文件 => upload文件夹 => ftp服务器
     *
     * @param file 要上传的文件
     * @param path 文件保存的路径(例如:c:/program file/)
     * @return
     */
    public String uploadFile(MultipartFile file, String path) {
        //获取源文件名
        String fileName = file.getOriginalFilename();
        //获取文件的扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //设置上传后的名字,uuid保证名字不重复
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        System.out.println(fileName + " : " + fileExtensionName + " : " + uploadFileName);

        //判断目录是否存在,不存在则创建目录
        java.io.File fileDir = new java.io.File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        java.io.File targetFile = new java.io.File(path, uploadFileName);

        try {
            //文件上传指定目录
            file.transferTo(targetFile);

            //将指定目录下的文件批量上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //将指定目录下的文件删除
            targetFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile.getName();

    }

    /**
     * 在数据库中增加文件记录
     *
     * @param userId 用户Id
     * @param file   保存在数据库中的记录
     * @return
     */
    @Override
    public ServerResponse addFile(Integer userId, File file) {
        // 获取upload文件夹的相对路径
        File newfile = new File();
        newfile.setName(file.getName());
        newfile.setStatus(Const.FileStatusEnum.UNCHECKED.getCode());
        newfile.setAddress(PropertiesUtil.getProperty("ftp.server.http.prefix") + file.getAddress());
        newfile.setCategoryId(file.getCategoryId());
        newfile.setUploadUserId(userId);
        newfile.setCreateTime(new Date());
        newfile.setUpdateTime(new Date());
        newfile.setDetail(file.getDetail());
        newfile.setScore(file.getScore());


        int rowCount = fileMapper.insertSelective(newfile);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("上传文件成功");
        }
        return ServerResponse.createByErrorMessage("上传文件失败");
    }


}
