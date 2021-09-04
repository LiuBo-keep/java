package com.hp.jwt.controller;

import com.hp.jwt.bean.RegisterRequest;
import com.hp.jwt.common.Response;
import com.hp.jwt.service.RegisterService;
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
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Response userRegister(@RequestBody RegisterRequest request) {
        return new Response(registerService.register(request));
    }
}
