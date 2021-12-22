package org.springframework.security.authentication.jaas;

import java.util.List;
import javax.security.auth.login.LoginContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JaasAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final long serialVersionUID = 550L;
    private final transient LoginContext loginContext;

    public JaasAuthenticationToken(Object principal, Object credentials, LoginContext loginContext) {
        super(principal, credentials);
        this.loginContext = loginContext;
    }

    public JaasAuthenticationToken(Object principal, Object credentials, List<GrantedAuthority> authorities, LoginContext loginContext) {
        super(principal, credentials, authorities);
        this.loginContext = loginContext;
    }

    public LoginContext getLoginContext() {
        return this.loginContext;
    }
}