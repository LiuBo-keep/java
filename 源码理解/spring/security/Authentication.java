package org.springframework.security.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;

// 认证成功后的信息
// 当前登录人的详细
// Authentication是SpringSecurity中认证的主体，包含主体权限列表、主体凭据、主体详细信息，以及主体是否验证成功等信息
//  /ɔːˌθentɪˈkeɪʃn/
public interface Authentication extends Principal, Serializable {
	// 获取权限
    Collection<? extends GrantedAuthority> getAuthorities();
	
	// 获取凭证 通常为密码
    Object getCredentials();
	
	// 获取详情
    Object getDetails();
	
	// 获取主体，通常为用户名
    Object getPrincipal();
	
	// 获取是否认证成功
    boolean isAuthenticated();
	
	// 设置是否认证成功
    void setAuthenticated(boolean var1) throws IllegalArgumentException;
}
