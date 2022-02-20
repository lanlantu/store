package com.cy.store.service;

import com.cy.store.entity.User;
import com.cy.store.service.ex.ServiceException;
import com.cy.store.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private IUserService userService;

    @Test
    public void reg() {
        try {
            User user = new User();
            user.setUsername("lower2");
            user.setPassword("123456");
            userService.reg(user);
            System.out.println("注册成功！");
        } catch (ServiceException e) {
            System.out.println("注册失败！" + e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }


    @Test
    public void login() {
        User lanlantu = userService.login("lanlantu", "123");
        System.out.println(lanlantu);
    }


    @Test
    public void changePassword() {
        try {
            userService.changePassword(13,"lanlantu2","66666","123");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }





}
