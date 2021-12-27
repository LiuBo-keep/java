package org.springframework.security.authentication.jaas;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationFailedEvent;
import org.springframework.security.authentication.jaas.event.JaasAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

// AuthenticationProvider 认证流程接口
// ApplicationEventPublisherAware 应用事件推送感知器
// InitializingBean 初始化bean接口
// ApplicationListener<SessionDestroyedEvent>  应用监听接口 监听session销毁事件
public abstract class AbstractJaasAuthenticationProvider implements AuthenticationProvider, ApplicationEventPublisherAware, InitializingBean, ApplicationListener<SessionDestroyedEvent> {
    private ApplicationEventPublisher applicationEventPublisher;
    private AuthorityGranter[] authorityGranters;
    private JaasAuthenticationCallbackHandler[] callbackHandlers;
    protected final Log log = LogFactory.getLog(this.getClass());
    private LoginExceptionResolver loginExceptionResolver = new DefaultLoginExceptionResolver();
    private String loginContextName = "SPRINGSECURITY";

    public AbstractJaasAuthenticationProvider() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(this.loginContextName, "loginContextName cannot be null or empty");
        Assert.notEmpty(this.authorityGranters, "authorityGranters cannot be null or empty");
        if (ObjectUtils.isEmpty(this.callbackHandlers)) {
            this.setCallbackHandlers(new JaasAuthenticationCallbackHandler[]{new JaasNameCallbackHandler(), new JaasPasswordCallbackHandler()});
        }

        Assert.notNull(this.loginExceptionResolver, "loginExceptionResolver cannot be null");
    }

    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (!(auth instanceof UsernamePasswordAuthenticationToken)) {
            return null;
        } else {
            UsernamePasswordAuthenticationToken request = (UsernamePasswordAuthenticationToken)auth;

            try {
                LoginContext loginContext = this.createLoginContext(new AbstractJaasAuthenticationProvider.InternalCallbackHandler(auth));
                loginContext.login();
                Set<Principal> principals = loginContext.getSubject().getPrincipals();
                Set<GrantedAuthority> authorities = this.getAuthorities(principals);
                JaasAuthenticationToken result = new JaasAuthenticationToken(request.getPrincipal(), request.getCredentials(), new ArrayList(authorities), loginContext);
                this.publishSuccessEvent(result);
                return result;
            } catch (LoginException var7) {
                AuthenticationException resolvedException = this.loginExceptionResolver.resolveException(var7);
                this.publishFailureEvent(request, resolvedException);
                throw resolvedException;
            }
        }
    }

    private Set<GrantedAuthority> getAuthorities(Set<Principal> principals) {
        Set<GrantedAuthority> authorities = new HashSet();
        Iterator var3 = principals.iterator();

        while(var3.hasNext()) {
            Principal principal = (Principal)var3.next();
            AuthorityGranter[] var5 = this.authorityGranters;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                AuthorityGranter granter = var5[var7];
                Set<String> roles = granter.grant(principal);
                if (!CollectionUtils.isEmpty(roles)) {
                    Iterator var10 = roles.iterator();

                    while(var10.hasNext()) {
                        String role = (String)var10.next();
                        authorities.add(new JaasGrantedAuthority(role, principal));
                    }
                }
            }
        }

        return authorities;
    }

    protected abstract LoginContext createLoginContext(CallbackHandler var1) throws LoginException;

    protected void handleLogout(SessionDestroyedEvent event) {
        List<SecurityContext> contexts = event.getSecurityContexts();
        if (contexts.isEmpty()) {
            this.log.debug("The destroyed session has no SecurityContexts");
        } else {
            Iterator var3 = contexts.iterator();

            while(var3.hasNext()) {
                SecurityContext context = (SecurityContext)var3.next();
                Authentication auth = context.getAuthentication();
                if (auth != null && auth instanceof JaasAuthenticationToken) {
                    JaasAuthenticationToken token = (JaasAuthenticationToken)auth;

                    try {
                        LoginContext loginContext = token.getLoginContext();
                        this.logout(token, loginContext);
                    } catch (LoginException var8) {
                        this.log.warn("Error error logging out of LoginContext", var8);
                    }
                }
            }

        }
    }

    private void logout(JaasAuthenticationToken token, LoginContext loginContext) throws LoginException {
        if (loginContext != null) {
            this.log.debug(LogMessage.of(() -> {
                return "Logging principal: [" + token.getPrincipal() + "] out of LoginContext";
            }));
            loginContext.logout();
        } else {
            this.log.debug(LogMessage.of(() -> {
                return "Cannot logout principal: [" + token.getPrincipal() + "] from LoginContext. The LoginContext is unavailable";
            }));
        }
    }

    public void onApplicationEvent(SessionDestroyedEvent event) {
        this.handleLogout(event);
    }

    protected void publishFailureEvent(UsernamePasswordAuthenticationToken token, AuthenticationException ase) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(new JaasAuthenticationFailedEvent(token, ase));
        }

    }

    protected void publishSuccessEvent(UsernamePasswordAuthenticationToken token) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(new JaasAuthenticationSuccessEvent(token));
        }

    }

    AuthorityGranter[] getAuthorityGranters() {
        return this.authorityGranters;
    }

    public void setAuthorityGranters(AuthorityGranter[] authorityGranters) {
        this.authorityGranters = authorityGranters;
    }

    JaasAuthenticationCallbackHandler[] getCallbackHandlers() {
        return this.callbackHandlers;
    }

    public void setCallbackHandlers(JaasAuthenticationCallbackHandler[] callbackHandlers) {
        this.callbackHandlers = callbackHandlers;
    }

    String getLoginContextName() {
        return this.loginContextName;
    }

    public void setLoginContextName(String loginContextName) {
        this.loginContextName = loginContextName;
    }

    LoginExceptionResolver getLoginExceptionResolver() {
        return this.loginExceptionResolver;
    }

    public void setLoginExceptionResolver(LoginExceptionResolver loginExceptionResolver) {
        this.loginExceptionResolver = loginExceptionResolver;
    }

    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    protected ApplicationEventPublisher getApplicationEventPublisher() {
        return this.applicationEventPublisher;
    }

    private class InternalCallbackHandler implements CallbackHandler {
        private final Authentication authentication;

        InternalCallbackHandler(Authentication authentication) {
            this.authentication = authentication;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            JaasAuthenticationCallbackHandler[] var2 = AbstractJaasAuthenticationProvider.this.callbackHandlers;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                JaasAuthenticationCallbackHandler handler = var2[var4];
                Callback[] var6 = callbacks;
                int var7 = callbacks.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    Callback callback = var6[var8];
                    handler.handle(callback, this.authentication);
                }
            }

        }
    }
}
