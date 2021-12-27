package org.springframework.security.authentication.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;

// 抽象用户信息认证
public abstract class AbstractUserDetailsAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private UserCache userCache = new NullUserCache();
	// 强制主体为字符串默认为false
    private boolean forcePrincipalAsString = false;
	// 隐藏未找到的用户异常
    protected boolean hideUserNotFoundExceptions = true;
	// 默认前检查器
    private UserDetailsChecker preAuthenticationChecks = new AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks();
	// 默认的后身份验证检查
    private UserDetailsChecker postAuthenticationChecks = new AbstractUserDetailsAuthenticationProvider.DefaultPostAuthenticationChecks();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    // 构造器
    public AbstractUserDetailsAuthenticationProvider() {
    }
	
	// 其他身份验证检查  UserDetails用户详细信息  
    protected abstract void additionalAuthenticationChecks(UserDetails var1, UsernamePasswordAuthenticationToken var2) throws AuthenticationException;
	
	// 属性设置后
    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userCache, "A user cache must be set");
        Assert.notNull(this.messages, "A message source must be set");
        this.doAfterPropertiesSet();
    }
	
	// 验证信息
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 只能对UsernamePasswordAuthenticationToken进行认证
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication, () -> {
            return this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only UsernamePasswordAuthenticationToken is supported");
        });
		// 获取用户名
        String username = this.determineUsername(authentication);
		// 默认已使用缓存
        boolean cacheWasUsed = true;
		// NullUserCache使用getUserFromCache返回null
        UserDetails user = this.userCache.getUserFromCache(username);
		// 肯定成立
        if (user == null) {
			// 将缓存状态改为false
            cacheWasUsed = false;

            try {
				// 检索用户 
				// 使用DaoAuthenticationProvider类中的检索方法
				// 通过用户名获取用户信息
                user = this.retrieveUser(username, (UsernamePasswordAuthenticationToken)authentication);
            } catch (UsernameNotFoundException var6) {
                this.logger.debug("Failed to find user '" + username + "'");
                if (!this.hideUserNotFoundExceptions) {
                    throw var6;
                }

                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }

            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }

        try {
			// 用户信息检查
            this.preAuthenticationChecks.check(user);
			// 其他身份验证检查(使用DaoAuthenticationProvider类中的实现 对密码进行校验)
            this.additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken)authentication);
        } catch (AuthenticationException var7) {
            if (!cacheWasUsed) {
                throw var7;
            }

            cacheWasUsed = false;
			// // 检索用户
            user = this.retrieveUser(username, (UsernamePasswordAuthenticationToken)authentication);
			// // 用户信息检查
            this.preAuthenticationChecks.check(user);
			// 其他身份验证检查
            this.additionalAuthenticationChecks(user, (UsernamePasswordAuthenticationToken)authentication);
        }
		
		// // 用户信息检查
        this.postAuthenticationChecks.check(user);
		// 缓存用户
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
		
		// 返回当前认证用户信息
        Object principalToReturn = user;
		// 不进方法
        if (this.forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }
		// 创建成功身份验证 并返回 
        return this.createSuccessAuthentication(principalToReturn, authentication, user);
    }
	
	// 确定用户名
    private String determineUsername(Authentication authentication) {
        return authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
    }
	
	// 创建成功身份验证 principal 用户信息user  authentication  user
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
		// 创建UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, authentication.getCredentials(), this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());
        this.logger.debug("Authenticated user");
        return result;
    }

    protected void doAfterPropertiesSet() throws Exception {
    }

    public UserCache getUserCache() {
        return this.userCache;
    }

    public boolean isForcePrincipalAsString() {
        return this.forcePrincipalAsString;
    }

    public boolean isHideUserNotFoundExceptions() {
        return this.hideUserNotFoundExceptions;
    }
	
	// 检索用户此方法为抽象方法需要子类实现
    protected abstract UserDetails retrieveUser(String var1, UsernamePasswordAuthenticationToken var2) throws AuthenticationException;

    public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
        this.forcePrincipalAsString = forcePrincipalAsString;
    }

    public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
        this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setUserCache(UserCache userCache) {
        this.userCache = userCache;
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected UserDetailsChecker getPreAuthenticationChecks() {
        return this.preAuthenticationChecks;
    }

    public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
        this.preAuthenticationChecks = preAuthenticationChecks;
    }

    protected UserDetailsChecker getPostAuthenticationChecks() {
        return this.postAuthenticationChecks;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
        private DefaultPostAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            if (!user.isCredentialsNonExpired()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account credentials have expired");
                throw new CredentialsExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
        private DefaultPreAuthenticationChecks() {
        }

        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is locked");
                throw new LockedException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
            } else if (!user.isEnabled()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account is disabled");
                throw new DisabledException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
            } else if (!user.isAccountNonExpired()) {
                AbstractUserDetailsAuthenticationProvider.this.logger.debug("Failed to authenticate since user account has expired");
                throw new AccountExpiredException(AbstractUserDetailsAuthenticationProvider.this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
            }
        }
    }
}
