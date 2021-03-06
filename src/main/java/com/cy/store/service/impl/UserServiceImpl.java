package com.cy.store.service.impl;

import java.util.UUID;
import com.cy.store.entity.User;
import com.cy.store.mapper.UserMapper;
import com.cy.store.service.IUserService;
import com.cy.store.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

/** 处理用户数据的业务层实现类 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public void reg(User user) {
        // 根据参数user对象获取注册的用户名
        String username = user.getUsername();
        // 调用持久层的User findByUsername(String username)方法，根据用户名查询用户数据
        User result = userMapper.findByUsername(username);
        // 判断查询结果是否不为null
        if (result != null) {
            // 是：表示用户名已被占用，则抛出UsernameDuplicateException异常
            throw new UsernameDuplicateException("尝试注册的用户名[" + username + "]已经被占用");
        }

        // 创建当前时间对象
        Date now = new Date();
        // 补全数据：加密后的密码
        String salt = UUID.randomUUID().toString().toUpperCase();
        String md5Password = getMd5Password(user.getPassword(), salt);
        user.setPassword(md5Password);
        // 补全数据：盐值
        user.setSalt(salt);
        // 补全数据：isDelete(0)
        user.setIsDelete(0);
        // 补全数据：4项日志属性
        user.setCreatedUser(username);
        user.setCreatedTime(now);
        user.setModifiedUser(username);
        user.setModifiedTime(now);

        // 表示用户名没有被占用，则允许注册
        // 调用持久层Integer insert(User user)方法，执行注册并获取返回值(受影响的行数)
        Integer rows = userMapper.insert(user);
        // 判断受影响的行数是否不为1
        if (rows != 1) {
            // 是：插入数据时出现某种错误，则抛出InsertException异常
            throw new InsertException("添加用户数据出现未知错误，请联系系统管理员");
        }
    }

    @Override
    public User login(String username, String password) {
        User result = userMapper.findByUsername(username);
        if (result==null||result.getIsDelete()==1){
            throw new UserNotFoundException("用户["+username+"]数据不存在");
        }
        String salt = result.getSalt();
        String oldMd5Password = result.getPassword();
        String newMd5Password = getMd5Password(password,salt);

        if (!newMd5Password.equals(oldMd5Password)){
            throw new PasswordNotMatchException("用户密码错误");
        }
        // 创建新的User对象
        User user = new User();
        // 将查询结果中的uid、username、avatar封装到新的user对象中
        user.setUid(result.getUid());
        user.setUsername(result.getUsername());
        user.setAvatar(result.getAvatar());
        return user;
    }

    @Override
    public void changePassword(Integer uid, String username, String oldPassword, String newPassword) {
        User result= userMapper.findByUid(uid);
        if (result==null|| result.getIsDelete()==1){
            throw new UserNotFoundException("用户["+username+"]数据不存在");
        }
        String oldMd5Password = getMd5Password(oldPassword, result.getSalt());
        if (!result.getPassword().equals(oldMd5Password)){
            throw new PasswordNotMatchException("原密码不正确，请重新输入");
        }

        String latestPassword = getMd5Password(newPassword, result.getSalt());

        Integer rows = userMapper.updatePasswordByUid(uid, latestPassword, username, new Date());
        if (rows!= 1){
            throw new UpdateException("更新数据产生未知异常");
        }
    }

    @Override
    public User getByUid(Integer uid) {
        User result = userMapper.findByUid(uid);
        if (result == null || result.getIsDelete()==1){
            throw new UserNotFoundException("用户数据不存在");
        }
        User user = new User();
        user.setUsername(result.getUsername());
        user.setPhone(result.getPhone());
        user.setEmail(result.getEmail());
        user.setGender(result.getGender());

        return user;
    }

    @Override
    public void changeInfo(Integer uid, String username, User user) {
        User result = userMapper.findByUid(uid);
        if (result == null || result.getIsDelete() == 1){
            throw new UserNotFoundException("用户数据已经被删除");
        }

        user.setUid(uid);
        user.setModifiedUser(username);
        user.setModifiedTime(new Date());

        Integer rows= userMapper.updateInfoByUid(user);
        if (rows!=1){
            throw new UpdateException("更新数据时产生未知异常");
        }

    }

    @Override
    public void changeAvatar(Integer uid, String username, String avatar) {
        User result = userMapper.findByUid(uid);
        if (result == null) {
            throw new UserNotFoundException("用户数据不存在");
        }

        if (result.getIsDelete().equals(1)) {
            throw new UserNotFoundException("用户数据不存在");
        }

        Date now = new Date();
        Integer rows = userMapper.updateAvatarByUid(uid, avatar, username, now);
        if (rows != 1) {
            throw new UpdateException("更新用户数据时出现未知错误，请联系系统管理员");
        }
    }


    /**
     * 执行密码加密
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密文
     */
    private String getMd5Password(String password, String salt) {
        /*
         * 加密规则：
         * 1、无视原始密码的强度
         * 2、使用UUID作为盐值，在原始密码的左右两侧拼接
         * 3、循环加密3次
         */
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();
        }
        return password;
    }

}
