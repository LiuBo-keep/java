package org.springframework.security.core.userdetails.cache;

import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

// 空用户缓存
public class NullUserCache implements UserCache {
	
	// 构造器
    public NullUserCache() {
    }
	
	// 通过名称获取返回null
    public UserDetails getUserFromCache(String username) {
        return null;
    }

    public void putUserInCache(UserDetails user) {
    }

    public void removeUserFromCache(String username) {
    }
}