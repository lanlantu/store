package com.cy.store.mapper;

import com.cy.store.entity.User;


import com.cy.store.service.ex.PasswordNotMatchException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
//@RunWith(SpringRunner.class)
public class UserMapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insert(){
        User user = new User();
        user.setUsername("user02");
        user.setPassword("123456");
        Integer rows = userMapper.insert(user);
        System.out.println("rows=" + rows);
    }

    @Test
    public void findByUsername() {
        User user05 = userMapper.findByUsername("user05");
        System.out.println(user05+"\\\\\\");
    }

    @Test
    public void  updateByUid(){
        Integer integer = userMapper.updatePasswordByUid(15, "333", "lanlantu", new Date());
        System.out.println(integer);
    }
}
