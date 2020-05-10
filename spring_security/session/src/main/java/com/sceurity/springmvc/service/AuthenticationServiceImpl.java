package com.sceurity.springmvc.service;

import com.sceurity.springmvc.dao.AuthenticationDao;
import com.sceurity.springmvc.model.AuthenticationRequest;
import com.sceurity.springmvc.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @ClassName AuthenticationServiceImpl
 * @Description 校验用户的身份信息是否合法
 * @Author 17126
 * @Date 2020/5/10 21:29
 */

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    @Autowired
    AuthenticationDao authenticationDao;

    @Override
    public UserDto anthentication(AuthenticationRequest authenticationRequest) {
        if (null==authenticationRequest
                || StringUtils.isEmpty(authenticationRequest.getUsername())
                || StringUtils.isEmpty(authenticationRequest.getPassword())){

            throw new RuntimeException("账号或密码为空");
        }

        //模仿查询数据库
        UserDto userDto = authenticationDao.getUserDao(authenticationRequest.getUsername());
        if (null==userDto){
            throw new RuntimeException("查询不到该用户");
        }
        //校验密码
        if (!authenticationRequest.getPassword().equals(userDto.getPassword())){
            throw new RuntimeException("账号或密码错误");
        }

        //认证通过，返回用户信息
        return userDto;
    }
}
