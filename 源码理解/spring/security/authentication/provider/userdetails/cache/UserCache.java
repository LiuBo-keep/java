package org.springframework.security.core.userdetails;

// 用户信息缓存
public interface UserCache {
	
	// 通过用户名获取用户信息
    UserDetails getUserFromCache(String username);
	
	// 向缓存中put用户信息
    void putUserInCache(UserDetails user);
	
	// 从缓存中移除用户信息通过名称
    void removeUserFromCache(String username);
}
