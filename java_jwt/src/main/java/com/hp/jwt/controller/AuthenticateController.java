package com.hp.jwt.controller;

import com.hp.jwt.bean.RegisterRequest;
import com.hp.jwt.common.Response;
import com.hp.jwt.service.AuthenticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@RestController
@RequestMapping("/authenticates")
public class AuthenticateController {

    @Autowired
    AuthenticateService authenticateService;

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public Object getToken(@RequestBody RegisterRequest request) {
        return new Response(authenticateService.authenticate(request));
    }
}
