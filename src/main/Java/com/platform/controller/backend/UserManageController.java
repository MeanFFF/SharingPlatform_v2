package com.platform.controller.backend;

import com.github.pagehelper.PageInfo;
import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;
import com.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 管理员用户控制块
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理员登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
            }
        }
        return response;
    }


    /**
     * 获取普通用户列表
     *
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "user_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iUserService.getList(pageNum, pageSize, Const.SearchUserTag.NORMAL);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 搜索用户名, 邮箱得到用户列表
     *
     * @param session
     * @param username 用户名, 模糊匹配(like)
     * @param email    邮箱
     * @param pageNum  页码, 默认为1
     * @param pageSize 每一页有多少项, 默认为10
     * @return
     */
    @RequestMapping(value = "search_list", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpSession session, String username, String email, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iUserService.getSearchList(username, email, pageNum, pageSize, null);
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    /**
     * 排序
     *
     * @param session
     * @param tag      1-根据用户积分数降序, 2-根据用户累计消耗积分数降序
     * @param pageNum  页码, 默认为1
     * @param pageSize 每一页有多少项, 默认为10
     * @return
     */
    @RequestMapping(value = "order_by", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> orderBy(HttpSession session, Integer tag, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            if (tag == Const.SearchUserTag.ORDER_BY_SOCRE || tag == Const.SearchUserTag.ORDER_BY_RESUME_SOCRE) {
                return iUserService.getList(pageNum, pageSize, tag);
            } else {
                return ServerResponse.createByErrorMessage("获取列表错误");
            }

        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    @RequestMapping(value = "set_user_status", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setUserStatus(HttpSession session, Integer userId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            if ((status == Const.UserStatus.NOT_ACTIVE || status == Const.UserStatus.DELETE || status == Const.UserStatus.NORMAL)
                && userId != null) {
                return iUserService.setUserStatus(userId, status);
            } else {
                return ServerResponse.createByErrorMessage("修改状态错误");
            }

        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse saveOrUpdateUser(HttpSession session, User newUser) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            if( newUser == null){
                return ServerResponse.createByErrorMessage("添加/修改用户信息错误");
            }
            return iUserService.saveOrUpdateUser(newUser);


        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

    @RequestMapping(value = "detail", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getUserDetail(HttpSession session, Integer userId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            if(userId == null){
                return ServerResponse.createByErrorMessage("修改积分错误");
            }
            return iUserService.getUserDetail(userId);

        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }


}
