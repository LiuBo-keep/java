package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

// 实际上对Authentication进行验证的类
// 实现类是对每种认证方式的真正认证过程	
public interface AuthenticationProvider {
	// 验证过程，如果成功则返回一个验证成功的Authentication
    Authentication authenticate(Authentication var1) throws AuthenticationException;
	// 判断该AUthentication是否支持
	// 当前认证流程是否支持当前认证方式
    boolean supports(Class<?> var1);
}