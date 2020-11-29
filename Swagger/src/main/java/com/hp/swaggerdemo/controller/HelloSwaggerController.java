package com.hp.swaggerdemo.controller;

import com.hp.swaggerdemo.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HelloSwaggerController
 * @Description TODO
 * @Author 17126
 * @Date 2020/11/29 12:59
 */

@Api("Hello类")
@RestController
public class HelloSwaggerController {

    @ApiOperation("打印hello")
    @RequestMapping(value = "/hello/{id}", method = RequestMethod.GET)
    public User hello(
            @ApiParam(value = "id", name = "接受id")
            @PathVariable("id") String id) {
        System.out.println(id + "Hello Swagger");
        return new User();
    }
}
