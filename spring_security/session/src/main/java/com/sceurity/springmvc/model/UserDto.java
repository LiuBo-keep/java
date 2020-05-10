package com.sceurity.springmvc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName UserDto
 * @Description 认证成功后返回的用户详细信息，也就是当前登录用户的信息
 * @Author 17126
 * @Date 2020/5/10 21:24
 */
@Data
@AllArgsConstructor
public class UserDto {

    public static final String SESSION_USER_KEY="_user";

    //用户身份信息
    private String id;
    private String username;
    private String password;
    private String fullname;
    private String mobile;
}
