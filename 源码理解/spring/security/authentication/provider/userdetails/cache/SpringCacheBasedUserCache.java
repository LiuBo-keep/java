package org.springframework.security.core.userdetails.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

// 
public class SpringCacheBasedUserCache implements UserCache {
    private static final Log logger = LogFactory.getLog(SpringCacheBasedUserCache.class);
    private final Cache cache;

    public SpringCacheBasedUserCache(Cache cache) {
        Assert.notNull(cache, "cache mandatory");
        this.cache = cache;
    }

    public UserDetails getUserFromCache(String username) {
        ValueWrapper element = username != null ? this.cache.get(username) : null;
        logger.debug(LogMessage.of(() -> {
            return "Cache hit: " + (element != null) + "; username: " + username;
        }));
        return element != null ? (UserDetails)element.get() : null;
    }

    public void putUserInCache(UserDetails user) {
        logger.debug(LogMessage.of(() -> {
            return "Cache put: " + user.getUsername();
        }));
        this.cache.put(user.getUsername(), user);
    }

    public void removeUserFromCache(UserDetails user) {
        logger.debug(LogMessage.of(() -> {
            return "Cache remove: " + user.getUsername();
        }));
        this.removeUserFromCache(user.getUsername());
    }

    public void removeUserFromCache(String username) {
        this.cache.evict(username);
    }
}
