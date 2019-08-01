package com.platform.redisService.impl;

import com.platform.dao.CategoryMapper;
import com.platform.dao.SignInMapper;
import com.platform.dao.UserMapper;
import com.platform.pojo.SignIn;
import com.platform.pojo.User;
import com.platform.redisService.IRedisUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisUserServiceImpl implements IRedisUserService {
    @Autowired
    private RedisCacheUtil redisCacheUtil;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SignInMapper signInMapper;

//    public List<Category> getChildrenParallelCategoryCache(Integer parentId){
////        String str = (String) redisCacheUtil.get("categoryList:"+parentId);
////        List<Category> categoryList = JSON.parseArray(str, Category.class);
//        List<Category> categoryList = (List<Category>) redisCacheUtil.getArrayFromJSON("categoryList:"+parentId, Category.class);
//        if(categoryList == null){
//            categoryList = categoryMapper.selectCategoryChildrenByParentId(parentId);
//            if(categoryList != null){
//                redisCacheUtil.setToJSON("categoryList:"+parentId, categoryList);
//            }
//        }
//        return categoryList;
//    }

    public User getUser(Integer userId) {
        User user = redisCacheUtil.getHashToObjectBykey("user:" + userId, User.class);
        // 会创建一个file的空对象, 不能判断file为null
        if (user.getId() == null) {
            user = userMapper.selectByPrimaryKey(userId);
            if(user != null){
                this.setOrUpdateUserCache(user);
            }
        }
        // 返回的file可能为空
        return user;
    }

    public void setOrUpdateUserCache(User user) {
        redisCacheUtil.delete("user:"+user.getId());
        redisCacheUtil.setObjectToHash("user:"+user.getId(), user);
    }

//    public void updateUserScoreCache(Integer userId, Integer addscore){
//        User user = this.getUser(userId);
//        if(user.getId() != null){
//            user.setTotalScore(user.getTotalScore() + addscore);
//            this.setOrUpdateUserCache(user);
//        }
//    }
//
//    public void updateUserStatusCache(Integer userId, Integer status){
//        User user = this.getUser(userId);
//        if(user.getId() != null){
//            user.setStatus(status);
//            this.setOrUpdateUserCache(user);
//        }
//    }
//
//    public void updateUserPwdCache(Integer userId, String newPwd){
//        User user = this.getUser(userId);
//        if(user.getId() != null){
//            user.setPassword(newPwd);
//            this.setOrUpdateUserCache(user);
//        }
//    }

    public void updateUserInfoCache(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        this.setOrUpdateUserCache(user);
    }


    // 修改密码部分
    public void setResetPwdUUid(String uuid, Integer userId){
        redisCacheUtil.set(uuid, userId);
    }

    public Integer getResetPwdUserId(String uuid){
        return (Integer) redisCacheUtil.get(uuid);
    }

    public void delete(String key){
        redisCacheUtil.delete(key);
    }


    // 签到部分
    public SignIn getSignInInfo(Integer userId){
        SignIn signIn = redisCacheUtil.getHashToObjectBykey("signIn:"+userId, SignIn.class);
        if(signIn.getId() == null){
            signIn = signInMapper.selectByUserId(userId);
            if(signIn != null){
                redisCacheUtil.setObjectToHash("signIn:"+userId, signIn);
            }
        }
        return signIn;
    }

    public void updateSignInfoCache(Integer userId){
        SignIn signIn = signInMapper.selectByUserId(userId);
        redisCacheUtil.delete("signIn:"+userId);
        redisCacheUtil.setObjectToHash("signIn:"+userId, signIn);
    }


}
