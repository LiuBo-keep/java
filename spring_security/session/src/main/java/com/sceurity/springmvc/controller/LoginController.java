package com.sceurity.springmvc.controller;

import com.sceurity.springmvc.model.AuthenticationRequest;
import com.sceurity.springmvc.model.UserDto;
import com.sceurity.springmvc.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @ClassName LoginController
 * @Description 登录Controller，对/Login请求进行处理，他调用AuthenticationRequestService完成认证并返回登录结果提示信息
 * @Author 17126
 * @Date 2020/5/10 21:47
 */

@RestController
public class LoginController {

    @Autowired
    AuthenticationService authenticationService;

    //登录
    //produces = {"text/plain;charset=UTF-8"}表示返回纯文本类型
    @PostMapping(value = "/login",produces = {"text/plain;charset=UTF-8"})
    public String login(AuthenticationRequest authenticationRequest, HttpSession session){
        UserDto userDto = authenticationService.anthentication(authenticationRequest);
        //用户信息存入session
        session.setAttribute(UserDto.SESSION_USER_KEY,userDto);
        return userDto.getFullname()+"登录成功";
    }

    //测试资源
    @GetMapping(value = "/r/r1",produces = {"text/plain;charset=UTF-8"})
    public String r1(HttpSession session){
        String fullname=null;
        Object obj = session.getAttribute(UserDto.SESSION_USER_KEY);

        if (null!=obj){
            fullname = ((UserDto)obj).getFullname();
        }else {
            fullname="匿名";
        }
        return fullname+"访问资源1";
    }

    //退出
    @GetMapping(value = "logout",produces = {"text/plain;charset=UTF-8"})
    public String logout(HttpSession session){
        //移除session
        session.invalidate();
        return "退出成功";
    }
}
