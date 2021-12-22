package org.springframework.security.authentication.jaas;

import java.util.List;
import javax.security.auth.login.LoginContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

// Jaas 认证令牌
public class JaasAuthenticationToken extends UsernamePasswordAuthenticationToken {
	// 序列号id
    private static final long serialVersionUID = 550L;
	// 登录上下文
    private final transient LoginContext loginContext;
	
	// 构造器 初始化 主体 证书 登录上下文
    public JaasAuthenticationToken(Object principal, Object credentials, LoginContext loginContext) {
        super(principal, credentials);
        this.loginContext = loginContext;
    }
	
	// 构造器 初始化 主体 证书 权限
    public JaasAuthenticationToken(Object principal, Object credentials, List<GrantedAuthority> authorities, LoginContext loginContext) {
        super(principal, credentials, authorities);
        this.loginContext = loginContext;
    }
	
	// 获取登录上下文
    public LoginContext getLoginContext() {
        return this.loginContext;
    }
}
