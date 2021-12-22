package org.springframework.security.authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

// token认证
// CredentialsContainer 凭证容器接口
// public interface CredentialsContainer {
//	 擦除凭据
//    void eraseCredentials();
// }
public abstract class AbstractAuthenticationToken implements Authentication, CredentialsContainer {
	// 授权 GrantedAuthority
	// public interface GrantedAuthority extends Serializable {
	//   获得权限	
    //   String getAuthority();
    // }
	// 所以权限
    private final Collection<GrantedAuthority> authorities;
	// 详情对象
    private Object details;
	// 是否已认证 默认为false
    private boolean authenticated = false;
	
	// 构造器 初始化 权限
    public AbstractAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
		// 如果为null
		// 没有任何权限
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
        } else {
			// 初始化权限
            Iterator var2 = authorities.iterator();
            while(var2.hasNext()) {
                GrantedAuthority a = (GrantedAuthority)var2.next();
                Assert.notNull(a, "Authorities collection cannot contain any null elements");
            }
            this.authorities = Collections.unmodifiableList(new ArrayList(authorities));
        }
    }
	
	// 获取权限
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
	
	// 获取名称
    public String getName() {
		// 任何当前主体属于用户详情
		// 默认都使用UserDetails
        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails)this.getPrincipal()).getUsername();
		// AuthenticatedPrincipal仅是接口没有实现	
        } else if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal)this.getPrincipal()).getName();
        } else if (this.getPrincipal() instanceof Principal) {
            return ((Principal)this.getPrincipal()).getName();
        } else {
            return this.getPrincipal() == null ? "" : this.getPrincipal().toString();
        }
    }
	
	// 获取是否已认证
    public boolean isAuthenticated() {
        return this.authenticated;
    }
	
	// 设置是否已认证
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Object getDetails() {
        return this.details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public void eraseCredentials() {
        this.eraseSecret(this.getCredentials());
        this.eraseSecret(this.getPrincipal());
        this.eraseSecret(this.details);
    }

    private void eraseSecret(Object secret) {
        if (secret instanceof CredentialsContainer) {
            ((CredentialsContainer)secret).eraseCredentials();
        }

    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractAuthenticationToken)) {
            return false;
        } else {
            AbstractAuthenticationToken test = (AbstractAuthenticationToken)obj;
            if (!this.authorities.equals(test.authorities)) {
                return false;
            } else if (this.details == null && test.getDetails() != null) {
                return false;
            } else if (this.details != null && test.getDetails() == null) {
                return false;
            } else if (this.details != null && !this.details.equals(test.getDetails())) {
                return false;
            } else if (this.getCredentials() == null && test.getCredentials() != null) {
                return false;
            } else if (this.getCredentials() != null && !this.getCredentials().equals(test.getCredentials())) {
                return false;
            } else if (this.getPrincipal() == null && test.getPrincipal() != null) {
                return false;
            } else if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
                return false;
            } else {
                return this.isAuthenticated() == test.isAuthenticated();
            }
        }
    }

    public int hashCode() {
        int code = 31;

        GrantedAuthority authority;
        for(Iterator var2 = this.authorities.iterator(); var2.hasNext(); code ^= authority.hashCode()) {
            authority = (GrantedAuthority)var2.next();
        }

        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }

        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }

        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }

        if (this.isAuthenticated()) {
            code ^= -37;
        }

        return code;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" [");
        sb.append("Principal=").append(this.getPrincipal()).append(", ");
        sb.append("Credentials=[PROTECTED], ");
        sb.append("Authenticated=").append(this.isAuthenticated()).append(", ");
        sb.append("Details=").append(this.getDetails()).append(", ");
        sb.append("Granted Authorities=").append(this.authorities);
        sb.append("]");
        return sb.toString();
    }
}
