package org.springframework.security.authentication;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

// 通过用户名和密码认证获取token
// 基于用户名和密码
public class UsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {
	// 序列化id
    private static final long serialVersionUID = 550L;
	// 主体
    private final Object principal;
	// 证书
    private Object credentials;
	
	// 构造器初始化 主体 证书
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super((Collection)null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }
	
	// 构造器初始化 主体 证书  权限
    public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }
	
	// 获取证书
    public Object getCredentials() {
        return this.credentials;
    }
	
	// 获取主体
    public Object getPrincipal() {
        return this.principal;
    }
	
	// 设置是否已认证
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }
	
	// 擦除凭据
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
