package org.springframework.security.authentication;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

// 测试认证token
public class TestingAuthenticationToken extends AbstractAuthenticationToken {
	// 序列号id
    private static final long serialVersionUID = 1L;
	// 证书 通常是密码
    private final Object credentials;
	// 主要的 通常是用户名
    private final Object principal;
	
	// 构造器 初始化 principal credentials
    public TestingAuthenticationToken(Object principal, Object credentials) {
        super((Collection)null);
        this.principal = principal;
        this.credentials = credentials;
    }
	
	// 构造器 初始化 principal  credentials  authorities
    public TestingAuthenticationToken(Object principal, Object credentials, String... authorities) {
        this(principal, credentials, AuthorityUtils.createAuthorityList(authorities));
    }

    public TestingAuthenticationToken(Object principal, Object credentials, List<GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }
}