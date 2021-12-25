package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

// 认证管理
// AuthenticationManager 其中可以包含多个AuthenticationProvider
public interface AuthenticationManager {
	
	// 认证
    Authentication authenticate(Authentication var1) throws AuthenticationException;
}