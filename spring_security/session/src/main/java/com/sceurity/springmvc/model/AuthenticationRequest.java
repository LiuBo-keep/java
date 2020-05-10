package com.sceurity.springmvc.model;

import lombok.Data;

/**
 * @ClassName AuthenticationRequest
 * @Description 请求认证，用户身份信息
 * @Author 17126
 * @Date 2020/5/10 21:24
 */

@Data
public class AuthenticationRequest {
    //用户名
    private String username;
    //密码
    private String password;
}
