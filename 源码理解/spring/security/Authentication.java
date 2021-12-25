package org.springframework.security.core;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;

// 认证成功后的信息
<<<<<<< HEAD
=======
// 当前登录人的详细
// Authentication是SpringSecurity中认证的主体，包含主体权限列表、主体凭据、主体详细信息，以及主体是否验证成功等信息
//  /ɔːˌθentɪˈkeɪʃn/
>>>>>>> 6b1a56499a5192e3fae7f9f49defae8684fdd3ce
public interface Authentication extends Principal, Serializable {
	// 获取权限
    Collection<? extends GrantedAuthority> getAuthorities();
	
<<<<<<< HEAD
	// 获取凭证
=======
	// 获取凭证 通常为密码
>>>>>>> 6b1a56499a5192e3fae7f9f49defae8684fdd3ce
    Object getCredentials();
	
	// 获取详情
    Object getDetails();
	
<<<<<<< HEAD
	// 获得校长
    Object getPrincipal();
	
	// 是否已认证
    boolean isAuthenticated();
	
	// 设置认证
=======
	// 获取主体，通常为用户名
    Object getPrincipal();
	
	// 获取是否认证成功
    boolean isAuthenticated();
	
	// 设置是否认证成功
>>>>>>> 6b1a56499a5192e3fae7f9f49defae8684fdd3ce
    void setAuthenticated(boolean var1) throws IllegalArgumentException;
}
