package com.platform.controller.portal;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;
import com.platform.service.ICategoryService;
import com.platform.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/file/")
public class FileController {

    @Autowired
    private IFileService iFileService;
    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 用户上传文件
     * @param session       会话
     * @param file          要保存在数据库中的文件记录
     * @return
     */
    @RequestMapping(value = "add_file", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addFile(HttpSession session,
                                 com.platform.pojo.File file) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.addFile(user.getId(),file);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,
                                 @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
                                 HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.uploadFile(uploadFile,path);

        Map fileMap = Maps.newHashMap();
        fileMap.put("address",targetFileName);
        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping(value = "upload_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getUploadList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.getUploadList(user.getId(), pageNum, pageSize);
    }

    // TODO 用户下载逻辑 要封装起来,达到要求才能够暴露下载地址!!!
    /**
     * 用户下载
     * @param  session
     * @param  fileId
     * @return
     */
    @RequestMapping(value = "download", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse downloadFile(HttpSession session, Integer fileId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        return iFileService.downloadFile(user.getId(), fileId);
//        try {
//            String localpath = "G:/ftpdownload/";
//            /**
//             * 先根据fileId从redis缓存中去真实文件名
//             * 如果redis中不存在,再从数据库中取
//             */
//            com.platform.pojo.File thisFile = fileMapper.selectSearchByIdAndName(fileId, null).get(0);
//            String filename = thisFile.getName();
//            String fileAddress = thisFile.getAddress();
//            fileAddress = fileAddress.substring(fileAddress.lastIndexOf("/") + 1);
//            File file = new File(localpath + filename);
//            if(file.exists()){
//                return ServerResponse.createBySuccess("文件已存在");
//            }else{
//                boolean dFlag = FTPUtil.downloadFile(filename, fileAddress, localpath);
//                if(dFlag){
//                    return ServerResponse.createBySuccess("文件下载成功");
//                }else {
//                    return ServerResponse.createByErrorMessage("文件下载失败");
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ServerResponse.createByErrorMessage("文件下载失败");
    }

    @RequestMapping(value = "download_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDownloadList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.getDownloadList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "collect", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse collectFile(HttpSession session, Integer fileId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.collectFile(user.getId(), fileId);
    }

    @RequestMapping(value = "un_collect", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse unCollectFile(HttpSession session, Integer fileId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.unCollectFile(user.getId(), fileId);
    }

    @RequestMapping(value = "collection_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCollectionList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }

        return iFileService.getCollectionList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping(value = "detail")
    @ResponseBody
    public ServerResponse getFileDetail(int fileId){
        return iFileService.getFileDetail(fileId);
    }

    @RequestMapping(value = "collect_status", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCollectStatus(int userId, int fileId){
        return iFileService.getCollectStatus(userId, fileId);
    }


    @RequestMapping("list")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iFileService.getFileByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }

    @RequestMapping("get_category")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
            return iCategoryService.getChildrenParallelCategory(categoryId);
    }


}
