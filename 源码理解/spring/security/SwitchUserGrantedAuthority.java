package org.springframework.security.web.authentication.switchuser;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

// 切换用户授权
public final class SwitchUserGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 550L;
	
	// 角色
    private final String role;
	// 来源
    private final Authentication source;

    public SwitchUserGrantedAuthority(String role, Authentication source) {
        Assert.notNull(role, "role cannot be null");
        Assert.notNull(source, "source cannot be null");
        this.role = role;
        this.source = source;
    }

    public Authentication getSource() {
        return this.source;
    }

    public String getAuthority() {
        return this.role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof SwitchUserGrantedAuthority)) {
            return false;
        } else {
            SwitchUserGrantedAuthority swa = (SwitchUserGrantedAuthority)obj;
            return this.role.equals(swa.role) && this.source.equals(swa.source);
        }
    }

    public int hashCode() {
        int result = this.role.hashCode();
        result = 31 * result + this.source.hashCode();
        return result;
    }

    public String toString() {
        return "Switch User Authority [" + this.role + "," + this.source + "]";
    }
}
