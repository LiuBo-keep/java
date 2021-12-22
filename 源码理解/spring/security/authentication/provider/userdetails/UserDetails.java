package org.springframework.security.core.userdetails;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

// 用户详情
public interface UserDetails extends Serializable {
	
	// 权限
    Collection<? extends GrantedAuthority> getAuthorities();
	
	// 密码
    String getPassword();
	
	// 用户名
    String getUsername();
	
	// 帐户未过期
    boolean isAccountNonExpired();
 
    // 帐户未锁定
    boolean isAccountNonLocked();
	
	// 凭证未过期
    boolean isCredentialsNonExpired();
	
	// 已启用
    boolean isEnabled();
}
