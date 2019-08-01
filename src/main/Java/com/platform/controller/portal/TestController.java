package com.platform.controller.portal;

import com.google.common.collect.Lists;
import com.platform.common.ServerResponse;
import com.platform.pojo.Category;
import com.platform.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/test/")
public class TestController {

    @Autowired
    private RedisCache cache;


    @RequestMapping("test1")
    @ResponseBody
    public ServerResponse test1(){
        User user1 = new User();
        user1.setId(312);
        User user2 = new User();
        user2.setId(123);
        User user3 = new User();
        user3.setId(132);
        User user4 = new User();
        user4.setId(321);

        List<User> users = Lists.newArrayList();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        cache.put("userList", users);

        return ServerResponse.createBySuccessMessage("success");

    }

    @RequestMapping("test2")
    @ResponseBody
    public ServerResponse test2(){
        return ServerResponse.createBySuccess();
    }


}
