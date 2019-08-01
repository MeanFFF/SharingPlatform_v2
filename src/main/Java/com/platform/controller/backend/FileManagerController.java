package com.platform.controller.backend;

import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;
import com.platform.service.IFileService;
import com.platform.service.IReviewService;
import com.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/file/")
public class FileManagerController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private IReviewService iReviewService;

    //获取文件列表
    @RequestMapping(value = "file_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.getFileList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    /**
     * 搜索文件列表
     * @param  session  
     * @param  fileId   
     * @param  fileName 
     * @param  pageNum  
     * @param  pageSize 
     * @return 
     */
    @RequestMapping("search_list")
    @ResponseBody
    public ServerResponse fileSearch(HttpSession session,
                                     Integer fileId,
                                     String fileName,
                                     @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.getSearchFileList(fileId, fileName, pageNum, pageSize, Const.SearchFileTag.NORMAL);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 未审核列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("unchecked_list")
    @ResponseBody
    public ServerResponse uncheckedList(HttpSession session,
                                     @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.getUncheckedFileList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 按照下载次数逆序列表
     * @param  session  
     * @param  pageNum  
     * @param  pageSize 
     * @return 
     */
    @RequestMapping("times_list")
    @ResponseBody
    public ServerResponse TimesList(HttpSession session,
                                       @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.getFileListOrderTimesDesc(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }



    /**
     * 获取文件详细信息
     * @param  session 
     * @param  fileId  
     * @return 
     */
    @RequestMapping("file_detail")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer fileId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.getManegeFileDetail(fileId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 设置文件的状态(0-还未审核 1-审核通过 2-审核未通过 3-删除)
     * @param  session 
     * @param  fileId  
     * @param  status  
     * @return 
     */
    @RequestMapping("set_file_status")
    @ResponseBody
    public ServerResponse setFileStatus(HttpSession session, Integer fileId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.setFileStatus(fileId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 更新文件信息
     * @param  session 
     * @param  file    
     * @return   
     */
    @RequestMapping("save")
    @ResponseBody
    public ServerResponse setFileInfo(HttpSession session, com.platform.pojo.File file) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iFileService.saveOrUpdateFile(file);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping(value = "set_review_status", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setReviewStatus(HttpSession session, Integer reviewId, Boolean status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iReviewService.setReviewStatus(reviewId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }



}
