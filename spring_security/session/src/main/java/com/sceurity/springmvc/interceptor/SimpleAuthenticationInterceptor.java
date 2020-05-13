package com.sceurity.springmvc.interceptor;

import com.sceurity.springmvc.model.UserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName SimpleAuthenticationInterceptor
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/11 22:04
 */
@Component
public class SimpleAuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        //读取会话信息
        Object obj=request.getSession().getAttribute(UserDto.SESSION_USER_KEY);
        if (null==obj){
            writeContent(response,"请登录");
        }

        UserDto userDto=(UserDto)obj;

        //请求URI
        String uri=request.getRequestURI();
        if (userDto.getAuthorities().contains("p1")&&uri.contains("/r1")){
            return true;
        }
        if (userDto.getAuthorities().contains("p2")&&uri.contains("/r2")){
            return true;
        }
        writeContent(response,"权限不足，拒绝访问");
        return false;
    }

    private void writeContent(HttpServletResponse response, String str) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(str);
        writer.close();
    }
}
