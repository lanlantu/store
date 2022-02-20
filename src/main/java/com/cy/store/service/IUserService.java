package com.cy.store.service;

import com.cy.store.entity.User;

import java.util.Date;

/**
 * 处理用户数据的业务层接口
 */
public interface IUserService {
    /**
     * 用户注册
     *
     * @param user 用户数据
     */
    void reg(User user);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户数据
     */
    User login(String username, String password);

    void changePassword(Integer uid,String username,String password,String newPassword);

}
