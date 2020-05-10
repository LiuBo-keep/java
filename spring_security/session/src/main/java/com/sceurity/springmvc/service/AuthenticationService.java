package com.sceurity.springmvc.service;

import com.sceurity.springmvc.model.AuthenticationRequest;
import com.sceurity.springmvc.model.UserDto;

/**
 * @ClassName AuthenticationService
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/10 21:20
 */
public interface AuthenticationService {

    /**
     *功能描述
     * 用户认证
     *@author
     *@date
      * @param authenticationRequest 用户认证请求(账户和密码)
     *@return 认证成功的用户信息
    */
    UserDto anthentication(AuthenticationRequest authenticationRequest);
}
