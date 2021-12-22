package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

// 测试认证实现
public class TestingAuthenticationProvider implements AuthenticationProvider {
    public TestingAuthenticationProvider() {
    }
	
	// 返回当前认证方式
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }
	
	// 判断该AUthentication是否支持
    public boolean supports(Class<?> authentication) {
		// 判断TestingAuthenticationToken是否是authentication的父类
        return TestingAuthenticationToken.class.isAssignableFrom(authentication);
    }
}