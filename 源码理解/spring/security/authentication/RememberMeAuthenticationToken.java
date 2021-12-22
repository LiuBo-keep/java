package org.springframework.security.authentication;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class RememberMeAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 550L;
    private final Object principal;
    private final int keyHash;

    public RememberMeAuthenticationToken(String key, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        if (key != null && !"".equals(key) && principal != null && !"".equals(principal)) {
            this.keyHash = key.hashCode();
            this.principal = principal;
            this.setAuthenticated(true);
        } else {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
    }

    private RememberMeAuthenticationToken(Integer keyHash, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.keyHash = keyHash;
        this.principal = principal;
        this.setAuthenticated(true);
    }

    public Object getCredentials() {
        return "";
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        } else if (obj instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken other = (RememberMeAuthenticationToken)obj;
            return this.getKeyHash() == other.getKeyHash();
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.keyHash;
        return result;
    }
}