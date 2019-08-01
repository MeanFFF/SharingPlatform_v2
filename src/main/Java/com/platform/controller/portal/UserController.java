package com.platform.controller.portal;


import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;
import com.platform.service.ISignInService;
import com.platform.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ISignInService iSignInService;

    /**
     * 登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service -> mybatis -> dao
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 激活用户
     * @param uuid
     * @return
     */
    @RequestMapping(value = "activate")
    @ResponseBody
    public ServerResponse activateUser(String uuid){
        return iUserService.activateUser(uuid);
    }

    /**
     * 管理员,普通用户退出登录
     * @param session
     * @return
     */
    @RequestMapping(value = "logout")
    @ResponseBody
    public ServerResponse logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 验证用户名/密码是否存在
     * @param str   value
     * @param type  username或email
     * @return
     */
    @RequestMapping(value = "check_valid", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);

    }

    /**
     * 获得session中的用户信息, 用于检查登录状态
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }


    /**
     * 获得数据库中的用户信息
     * @param  session
     * @return
     */
    @RequestMapping(value = "get_information",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

//    /**
//     * username不能被更新, email修改后也要验证, emm只能修改密码, //todo
//     * @param session
//     * @param user
//     * @return
//     */
//    @RequestMapping(value = "update_information", method = RequestMethod.POST)
//    @ResponseBody
//    public ServerResponse<User> update_information(HttpSession session, User user){
//        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
//        //登录状态下才能修改信息
//        if(currentUser == null){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//
//        //防止越权问题
//        user.setId(currentUser.getId());
//        user.setUsername(currentUser.getUsername());
//        ServerResponse<User> response = iUserService.updateInformation(user);
//        if(response.isSuccess()){
//            response.getData().setUsername(currentUser.getUsername());
//            session.setAttribute(Const.CURRENT_USER, response.getData());
//        }
//        return response;
//    }

    /**
     * 忘记密码, 发送邮件
     * @param session
     * @return
     */
    @RequestMapping(value = "login_send_code", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse loginSendCode(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.loginSendCode(currentUser);
    }

    /**
     * 判断uuid是否在缓存中存在
     * 如果存在, 返回1, 跳转到修改密码页面
     * 如果不存在, 返回0, 该用户不存在
     * @param uuid
     * @return
     */
    @RequestMapping(value = "check_reset")
    @ResponseBody
    public ServerResponse checkResetUUid(String uuid){
        return iUserService.checkUUid(uuid);
    }



    /**
     * 修改密码页面, 传入uuid与新的密码, 更新数据库, 跳转登录页面
     * @param uuid
     * @param newPwd
     * @return
     */
    @RequestMapping(value = "reset_password")
    @ResponseBody
    public ServerResponse changingPwd(String uuid, @RequestParam(value = "newPwd") String newPwd){
        if(StringUtils.isBlank(newPwd)){
            return ServerResponse.createByErrorMessage("密码不能为空");
        }
        return iUserService.resetPassword(uuid, newPwd);
    }

    /**
     * 签到功能
     * 第一次签到, 插入签到数据, 积分加200
     * 数据字典
     * @return
     */
    @RequestMapping(value = "sign_in")
    @ResponseBody
    public ServerResponse signIn(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"签到失败, 用户未登录");
        }
        Integer userId = user.getId();
        return iSignInService.signIn(userId);
    }

    /**
     * 获取用户签到状态
     * @param session
     * @return
     */
    @RequestMapping(value = "get_SignIn_status")
    @ResponseBody
    public ServerResponse getSignInStatus(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"签到失败, 用户未登录");
        }
        return iSignInService.getSignInStatus(user.getId());
    }

    @RequestMapping(value = "comment", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse comment(HttpSession session, Integer fileId, String content){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"评论失败, 用户未登录");
        }
        return iUserService.comment(user.getId(), fileId, content);
    }

}
