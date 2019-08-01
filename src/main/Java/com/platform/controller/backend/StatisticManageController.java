package com.platform.controller.backend;

import com.platform.common.Const;
import com.platform.common.ResponseCode;
import com.platform.common.ServerResponse;
import com.platform.pojo.User;
import com.platform.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage/statistic/")
public class StatisticManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping("base_count")
    @ResponseBody
    public ServerResponse getBaseCount(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "还未登录,请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iUserService.getBaseCount();
        } else {
            return ServerResponse.createByErrorMessage("没有权限");
        }
    }

}
