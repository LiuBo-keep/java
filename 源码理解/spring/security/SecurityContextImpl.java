package org.springframework.security.core.context;

import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;

// 安全上下文实现类
public class SecurityContextImpl implements SecurityContext {
	// 序列号id
    private static final long serialVersionUID = 550L;
	
	// 认证成功信息
    private Authentication authentication;

    public SecurityContextImpl() {
    }
	
	// 构造器初始化 认证成功信息
    public SecurityContextImpl(Authentication authentication) {
        this.authentication = authentication;
    }
	
	// 重写equals方法
    public boolean equals(Object obj) {
		// obj是否属于 SecurityContextImpl 
		// 不成立直接返回false
        if (obj instanceof SecurityContextImpl) {
            SecurityContextImpl other = (SecurityContextImpl)obj;
			// 再比较getAuthentication
            if (this.getAuthentication() == null && other.getAuthentication() == null) {
                return true;
            }

            if (this.getAuthentication() != null && other.getAuthentication() != null && this.getAuthentication().equals(other.getAuthentication())) {
                return true;
            }
        }

        return false;
    }
	
	// 获取认证成功信息
    public Authentication getAuthentication() {
        return this.authentication;
    }
	
	// 重新hashCode
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.authentication);
    }
	
	// 设置认证成功信息
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" [");
        if (this.authentication == null) {
            sb.append("Null authentication");
        } else {
            sb.append("Authentication=").append(this.authentication);
        }

        sb.append("]");
        return sb.toString();
    }
}
