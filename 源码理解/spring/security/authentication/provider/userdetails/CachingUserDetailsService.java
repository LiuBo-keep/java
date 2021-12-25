package org.springframework.security.authentication;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

// 缓存用户详细信息服务
public class CachingUserDetailsService implements UserDetailsService {
    private UserCache userCache = new NullUserCache();
	// 获取用户信息服务
    private final UserDetailsService delegate;
	// 构造器 初始化 获取用户信息的服务
    public CachingUserDetailsService(UserDetailsService delegate) {
        this.delegate = delegate;
    }
	
	// 获取用户缓存
    public UserCache getUserCache() {
        return this.userCache;
    }
	
	// 设置用户缓存
    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }
	
	// 通过用户名获取用户
    public UserDetails loadUserByUsername(String username) {
		// 从缓存中获取
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
			// 通过获取用户服务查询用户
            user = this.delegate.loadUserByUsername(username);
        }

        Assert.notNull(user, () -> {
            return "UserDetailsService " + this.delegate + " returned null for username " + username + ". This is an interface contract violation";
        });
		// 缓存 用户信息
        this.userCache.putUserInCache(user);
        return user;
    }
}