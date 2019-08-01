package com.platform.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.platform.common.Const;
import com.platform.common.ServerResponse;
import com.platform.dao.*;
import com.platform.pojo.Review;
import com.platform.pojo.User;
import com.platform.redisService.IRedisFileService;
import com.platform.redisService.IRedisUserService;
import com.platform.service.IUserService;
import com.platform.utils.EmailUtil;
import com.platform.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private IRedisFileService iRedisFileService;
    @Autowired
    private IRedisUserService iRedisUserService;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //TODO 密码登录MD5

        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        } else if (user.getStatus() == Const.UserStatus.NOT_ACTIVE) {
            return ServerResponse.createByErrorMessage("账户未激活，请前去激活");
        } else if (user.getStatus() == Const.UserStatus.DELETE) {
            return ServerResponse.createByErrorMessage("该账号已被封禁");
        }

        // 登录成功时, 把密码设为空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user) {
        // 验证用户名是否存在
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        // 验证email是否存在
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        // MD5加密
        // user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        // 初始化100积分
        user.setTotalScore(100);
        user.setResumeScore(0);
        user.setStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        try {
            int resultCount = userMapper.insert(user);
            if (resultCount > 0) {
                // todo 发送激活邮件
                String uuid = String.valueOf(UUID.randomUUID());
                // 此时主键还没有生成, 缓存中保存用户名, 用户名是唯一的
                EmailUtil.sendEmail(user.getEmail(), "激活账户", "<html>" +
                        "你的验证码如下<br>" + uuid + "</html>");
                user = userMapper.selectByUsername(user.getUsername());
                iRedisUserService.setResetPwdUUid(uuid, user.getId());
            } else {
                return ServerResponse.createByErrorMessage("注册失败, 请重新注册");
            }
        } catch (EmailException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("发送邮件失败, 请重新注册");
        }
        return ServerResponse.createBySuccessMessage("注册成功, 邮件已发送到你邮箱, 请你前去邮箱激活");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 用户不能更改信息, 只能修改密码
     */
//    @Override
//    public ServerResponse<User> updateInformation(User user) {
//        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
//        if (resultCount > 0) {
//            return ServerResponse.createByErrorMessage("email已经存在,请更换email再尝试更新");
//        }
//        User updateUser = new User();
//        updateUser.setId(user.getId());
//        updateUser.setEmail(user.getEmail());
//        updateUser.setPhone(user.getPhone());
//
//        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
//        if (updateCount > 0) {
//            return ServerResponse.createBySuccess("更新个人信息成功", user);
//        }
//
//        return ServerResponse.createByErrorMessage("更新个人信息失败");
//        return null;
//    }


    /**
     * 获取用户列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getList(int pageNum, int pageSize, Integer tag) {
        return getSearchList(null, null, pageNum, pageSize, tag);
    }

    @Override
    public ServerResponse<PageInfo> getSearchList(String username, String email, int pageNum, int pageSize, Integer tag) {
        PageHelper.startPage(pageNum, pageSize);

        List<User> userList = null;
        //从数据库中获得用户列表
        if (StringUtils.isBlank(username) && StringUtils.isBlank(email)) {
            switch (tag) {
                case Const.SearchUserTag.NORMAL:
                    userList = userMapper.selectList();
                    break;
                case Const.SearchUserTag.ORDER_BY_SOCRE:
                    userList = userMapper.selectListOrderByScoreDesc();
                    break;
                case Const.SearchUserTag.ORDER_BY_RESUME_SOCRE:
                    userList = userMapper.selectListOrderByResumeScoreDesc();
                    break;
                default:
                    return ServerResponse.createByErrorMessage("加载列表错误");
            }

        } else {
            if (StringUtils.isNoneBlank(username)) {
                username = new StringBuilder().append("%").append(username).append("%").toString();
            }
            userList = userMapper.selectListByUsernameOrEmail(username, email);
        }


        // userVo:你想要传给前端的包装数据
        // 创建一个userVo的list
        List<UserVo> userListVoList = Lists.newArrayList();

        // 将用户列表中的user => userVo
        // 管理员不显示
        for (User userItem : userList) {
            if (!checkAdminRole(userItem).isSuccess()) {
                UserVo userListVo = assembleUserVo(userItem);
                userListVoList.add(userListVo);
            }
        }

        PageInfo pageResult = new PageInfo(userList);
        pageResult.setList(userListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 根据用户名修改用户状态
     *
     * @param userId
     * @param status
     * @return
     */
    @Override
    public ServerResponse setUserStatus(Integer userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        int rowCount = userMapper.updateUserByPrimaryKeySelective(user);
        if (rowCount > 0) {
            iRedisUserService.updateUserInfoCache(userId);
            return ServerResponse.createBySuccessMessage("修改状态成功");
        }
        return ServerResponse.createByErrorMessage("修改状态失败");
    }

    /**
     * 获得用户详细信息
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse getUserDetail(Integer userId) {
        User user = iRedisUserService.getUser(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("没有此用户");
        }
        System.out.println(user.getTotalScore());
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse saveOrUpdateUser(User user) {
        user.setUpdateTime(new Date());
        if (user.getId() != null) {
            int rowCount = userMapper.updateUserByPrimaryKeySelective(user);
            if (rowCount > 0) {
                iRedisUserService.updateUserInfoCache(user.getId());
                return ServerResponse.createBySuccessMessage("更新用户信息成功");
            }
            return ServerResponse.createBySuccessMessage("更新用户信息失败");
        } else {
            user.setCreateTime(user.getUpdateTime());
            int rowCount = userMapper.insert(user);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("添加用户成功");
            }
            return ServerResponse.createBySuccessMessage("添加用户失败");
        }
    }

    @Override
    public ServerResponse getBaseCount() {
        Integer userCount = userMapper.getCount();
        Integer fileCount = fileMapper.getCount();
        Integer orderCount = orderMapper.getCount();
        Map map = Maps.newHashMap();
        map.put("userCount", userCount);
        map.put("fileCount", fileCount);
        map.put("orderCount", orderCount);
        return ServerResponse.createBySuccess(map);
    }

    @Override
    public ServerResponse activateUser(String uuid) {
        Integer userId = iRedisUserService.getResetPwdUserId(uuid);
        if (userId != null) {
            User user = iRedisUserService.getUser(userId);
            if (user != null) {
                user.setStatus(Const.UserStatus.NORMAL);
                int rowCount = userMapper.updateUserByPrimaryKeySelective(user);
                if (rowCount > 0) {
                    iRedisUserService.updateUserInfoCache(user.getId());
                    iRedisUserService.delete(uuid);
                    // 跳转登录页面
                    return ServerResponse.createBySuccessMessage("激活成功");
                }

            }
        }
        return ServerResponse.createByErrorMessage("激活失败");
    }


    public ServerResponse checkUUid(String uuid) {
        Integer userId = iRedisUserService.getResetPwdUserId(uuid);

        if (userId == null) {
            return ServerResponse.createByErrorMessage("该用户不存在");
        }
        Map map = new HashMap();
        map.put("uuid", uuid);

        return ServerResponse.createBySuccess("跳转修改页面", map);
    }

    @Override
    public ServerResponse comment(Integer userId, Integer fileId, String content) {
        String str = iRedisFileService.getDownloadCache(userId, fileId);
        if (str == null) {
            return ServerResponse.createByErrorMessage("没有下载文件,不能评价");
        }
        Review review = new Review();
        review.setFileId(fileId);
        review.setContent(content);
        review.setUserId(userId);
        review.setStatus(Const.ReviewStatusEnum.SHOW.getValue());
        review.setCreateTime(new Date());
        int rowCount = reviewMapper.insertSelective(review);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("评论成功");
        }
        return ServerResponse.createByErrorMessage("评论失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = iRedisUserService.getUser(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 发送修改密码的邮件
     *
     * @param currentUser
     * @return
     */
    @Override
    public ServerResponse loginSendCode(User currentUser) {
        String uuid = String.valueOf(UUID.randomUUID());
        iRedisUserService.setResetPwdUUid(uuid, currentUser.getId());
        try {
            EmailUtil.sendEmail(currentUser.getEmail(), "修改密码", "<html>" +
                    "你的验证码如下<br>" + uuid + "</html>");
        } catch (EmailException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("发送邮件失败");
        }
        return ServerResponse.createBySuccess("修改密码的验证码已发送到你邮件中");
    }


    @Override
    public ServerResponse resetPassword(String uuid, String newPwd) {
        Integer userId = iRedisUserService.getResetPwdUserId(uuid);

        User user = iRedisUserService.getUser(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("更新密码错误");
        }
        user.setPassword(newPwd);
        int rowCount = userMapper.updateUserByPrimaryKeySelective(user);
        if (rowCount > 0) {
            // 邮箱的链接, 链接只能使用一次
            // 修改成功后, 移除缓存中的uuid和用户名;缓存时间到了,也会移除(expire为1个小时)
            iRedisUserService.updateUserInfoCache(user.getId());
            iRedisUserService.delete(uuid);
            return ServerResponse.createBySuccess("更新密码成功了");
        } else {
            return ServerResponse.createBySuccess("更新密码失败");
        }
    }


    private UserVo assembleUserVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setUsername(user.getUsername());
        userVo.setEmail(user.getEmail());
        userVo.setStatus(user.getStatus());
        userVo.setRole(user.getRole());
        userVo.setTotalScore(user.getTotalScore());
        userVo.setResumeScore(user.getResumeScore());
        userVo.setCreateTime(user.getCreateTime());
        userVo.setUpdateTime(user.getUpdateTime());
        return userVo;
    }

    /**
     * 验证用户是否为管理员
     *
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
