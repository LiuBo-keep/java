package org.springframework.security.authentication;

// 身份验证管理器解析器
// 获取相应的认证解析器
public interface AuthenticationManagerResolver<C> {
    AuthenticationManager resolve(C var1);
}