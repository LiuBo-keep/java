package org.springframework.security.core;

import java.io.Serializable;

// 角色和权限共用GrantedAuthority接口，唯一的不同角色就是多了个前缀"ROLE_"
public interface GrantedAuthority extends Serializable {
    String getAuthority();
}