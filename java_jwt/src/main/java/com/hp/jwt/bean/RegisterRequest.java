package com.hp.jwt.bean;

import lombok.Data;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@Data
public class RegisterRequest {

    private String userName;

    private String password;

    private String companyName;

    private String clientId;

    private String clientSecret;

    private String type;
}
