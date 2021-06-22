package com.example.demo.controller;

import com.example.demo.bean.User;
import com.example.demo.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 用户注册
 */

@RestController
@RequestMapping("/user")
public class UserRegisterController {

    @Autowired
    private UserRegisterService userRegisterService;

    @RequestMapping("/register")
    public String register(User user) {
        //进行注册
        userRegisterService.register(user);
        return "[controller]注册用户成功！";
    }
}
