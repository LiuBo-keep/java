package com.hp.jwt.common;

import lombok.Data;

/**
 * @author LiuBo
 * @date 2021/9/4
 * @Description 描述
 */

@Data
public class Response {

    private Object date;

    private String errormsg;

    public Response(Object date) {
        this.date = date;
    }

    public Response(String errormsg) {
        this.errormsg = errormsg;
    }

    public Response(Object date, String errormsg) {
        this.date = date;
        this.errormsg = errormsg;
    }
}
