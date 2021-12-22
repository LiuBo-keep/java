package org.springframework.security.web.authentication.preauth;

import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class PreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 550L;
    private final Object principal;
    private final Object credentials;

    public PreAuthenticatedAuthenticationToken(Object aPrincipal, Object aCredentials) {
        super((Collection)null);
        this.principal = aPrincipal;
        this.credentials = aCredentials;
    }

    public PreAuthenticatedAuthenticationToken(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(anAuthorities);
        this.principal = aPrincipal;
        this.credentials = aCredentials;
        this.setAuthenticated(true);
    }

    public Object getCredentials() {
        return this.credentials;
    }

    public Object getPrincipal() {
        return this.principal;
    }
}