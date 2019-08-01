package com.platform.service.impl;

import com.google.common.collect.Maps;
import com.platform.common.Const;
import com.platform.common.ServerResponse;
import com.platform.dao.SignInMapper;
import com.platform.dao.UserMapper;
import com.platform.pojo.SignIn;
import com.platform.pojo.User;
import com.platform.redisService.IRedisUserService;
import com.platform.service.ISignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class SignInServiceImpl implements ISignInService {

    @Autowired
    private SignInMapper signInMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IRedisUserService iRedisUserService;

    // todo 事务
    public ServerResponse signIn(Integer userId) {
        // 判断签到表中有没有用户签到的记录
//        SignIn signInItem = signInMapper.selectByUserId(userId);
//        User user = userMapper.selectByPrimaryKey(userId);
        SignIn signInItem = iRedisUserService.getSignInInfo(userId);
        User user = iRedisUserService.getUser(userId);
        if (signInItem == null) {
            // 用户首次签到
            signInItem = new SignIn();
            signInItem.setUserId(userId);
            signInItem.setTime(new Date());
            signInItem.setDays(1);
            int rowCount = signInMapper.insert(signInItem);
            if (rowCount > 0) {
                int addScore = Const.SignInScoreEnum.FIRST_SIGN_IN.getValue();
                user.setTotalScore(user.getTotalScore() + addScore);
                userMapper.updateUserByPrimaryKeySelective(user);
                iRedisUserService.updateUserInfoCache(userId);
                return ServerResponse.createBySuccess("签到成功, 首次签到获得" + addScore + "积分");
            }
            return ServerResponse.createByErrorMessage("签到失败");
        } else {
            // 从数据库中获得连续签到的天数
            int days = signInMapper.selectDaysByUserId(userId);
            // 更新签到信息
            int rowCount = signInMapper.updateSignIn(userId);
            // 更新签到缓存
            iRedisUserService.updateSignInfoCache(userId);
            SignIn signInItem2 = signInMapper.selectByUserId(userId);

            if (rowCount > 0) {
                // 根据数据字典获得所要加的积分, 更新用户数据
                // 判断更新后时间会不会改变, 时间改变:加分, 没改变:今天已经签到过了
                 if(signInItem2.getTime().getTime() != signInItem.getTime().getTime()){
                     int addScore = Const.SignInScoreEnum.codeOf(days % 7 + 1).getValue();
                     user.setTotalScore(user.getTotalScore() + addScore);
                     userMapper.updateUserByPrimaryKeySelective(user);
                     iRedisUserService.updateUserInfoCache(userId);
                     return ServerResponse.createBySuccess("签到成功, 获得" + addScore +"积分");
                 }else{
                     return ServerResponse.createBySuccess("今天已经签到过了");
                 }
            } else {
                return ServerResponse.createByErrorMessage("签到失败");
            }
        }
    }

    @Override
    public ServerResponse getSignInStatus(Integer userId) {
//        SignIn signInItem = signInMapper.selectByUserId(userId);

        SignIn signInItem = iRedisUserService.getSignInInfo(userId);
        if(signInItem == null){
            return ServerResponse.createBySuccess("用户还未签过到, 首次签到获得:" + Const.SignInScoreEnum.FIRST_SIGN_IN.getValue() + "积分");
        }
        Map map = Maps.newHashMap();

        if(isToday(signInItem.getTime())){
            // 今天已经签过到了
            map.put("status",0);
            map.put("days",signInItem.getDays());
        }else{
            // 今天还未签到
            map.put("status",1);
            if(isYesterday(signInItem.getTime())){
                map.put("days",signInItem.getDays());
            }else{
                map.put("days",0);
            }
        }
        return ServerResponse.createBySuccess(map);
    }

    private static boolean isToday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);
        String now = sdf.format(new Date());
        return param.equals(now);
    }

    private static boolean isYesterday(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String param = sdf.format(date);
        Date yest = new Date(new Date().getTime() - 86400000L);
        String yesterday = sdf.format(yest);
        return param.equals(yesterday);
    }

}
