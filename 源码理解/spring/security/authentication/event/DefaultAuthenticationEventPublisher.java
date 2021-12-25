package org.springframework.security.authentication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProviderNotFoundEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProxyUntrustedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

// DefaultAuthenticationEventPublisher实现了ApplicationEventPublisherAware接口 告诉容器自己需要ApplicationEventPublisher的实现
// spring容器在初始化时会感知到，则会将ApplicationEventPublisher的实现注入到DefaultAuthenticationEventPublisher类的applicationEventPublisher属性上
// AuthenticationEventPublisher是认证实现相关发布操作
// DefaultAuthenticationEventPublisher会向容器要事件发布器，然后在相应的方法中使用此发布器
public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher, ApplicationEventPublisherAware {
    private final Log logger;
	// 应用事件发布器
    private ApplicationEventPublisher applicationEventPublisher;
	// 异常映射
    private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>> exceptionMappings;
	// 默认身份验证失败事件构造函数
    private Constructor<? extends AbstractAuthenticationFailureEvent> defaultAuthenticationFailureEventConstructor;
	
	// 构造器
    public DefaultAuthenticationEventPublisher() {
        this((ApplicationEventPublisher)null);
    }
	
	// 构造器 应用事件发布器
    public DefaultAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.logger = LogFactory.getLog(this.getClass());
		// 初始化 异常映射
        this.exceptionMappings = new HashMap();
		// 初始化 应用事件发布器
        this.applicationEventPublisher = applicationEventPublisher;
		// 注册各种异常事件
		// 错误凭据异常 
        this.addMapping(BadCredentialsException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
		// 找不到用户名异常
        this.addMapping(UsernameNotFoundException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
		// 帐户过期异常
        this.addMapping(AccountExpiredException.class.getName(), AuthenticationFailureExpiredEvent.class);
		// 提供者未找到异常
        this.addMapping(ProviderNotFoundException.class.getName(), AuthenticationFailureProviderNotFoundEvent.class);
		// 禁用异常
        this.addMapping(DisabledException.class.getName(), AuthenticationFailureDisabledEvent.class);
		// 锁定异常
        this.addMapping(LockedException.class.getName(), AuthenticationFailureLockedEvent.class);
		// 认证服务异常
        this.addMapping(AuthenticationServiceException.class.getName(), AuthenticationFailureServiceExceptionEvent.class);
		// 凭证过期异常
        this.addMapping(CredentialsExpiredException.class.getName(), AuthenticationFailureCredentialsExpiredEvent.class);
        this.addMapping("org.springframework.security.authentication.cas.ProxyUntrustedException", AuthenticationFailureProxyUntrustedEvent.class);
        this.addMapping("org.springframework.security.oauth2.server.resource.InvalidBearerTokenException", AuthenticationFailureBadCredentialsEvent.class);
    }
	
	// 发布认证成功
    public void publishAuthenticationSuccess(Authentication authentication) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
        }

    }
	
	// 发布认证失败
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        Constructor<? extends AbstractAuthenticationEvent> constructor = this.getEventConstructor(exception);
        AbstractAuthenticationEvent event = null;
        if (constructor != null) {
            try {
                event = (AbstractAuthenticationEvent)constructor.newInstance(authentication, exception);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException var6) {
            }
        }

        if (event != null) {
            if (this.applicationEventPublisher != null) {
                this.applicationEventPublisher.publishEvent(event);
            }
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("No event was found for the exception " + exception.getClass().getName());
        }

    }
	
	// 获取事件构造函数
    private Constructor<? extends AbstractAuthenticationEvent> getEventConstructor(AuthenticationException exception) {
        Constructor<? extends AbstractAuthenticationEvent> eventConstructor = (Constructor)this.exceptionMappings.get(exception.getClass().getName());
        return eventConstructor != null ? eventConstructor : this.defaultAuthenticationFailureEventConstructor;
    }
	
	// 设置应用程序事件发布者
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /** @deprecated */
    @Deprecated
    public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
        Assert.notNull(additionalExceptionMappings, "The exceptionMappings object must not be null");
        Iterator var2 = additionalExceptionMappings.keySet().iterator();

        while(var2.hasNext()) {
            Object exceptionClass = var2.next();
            String eventClass = (String)additionalExceptionMappings.get(exceptionClass);

            try {
                Class<?> clazz = this.getClass().getClassLoader().loadClass(eventClass);
                Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
                this.addMapping((String)exceptionClass, clazz);
            } catch (ClassNotFoundException var6) {
                throw new RuntimeException("Failed to load authentication event class " + eventClass);
            }
        }

    }
	
	// 设置附加异常映射
    public void setAdditionalExceptionMappings(Map<Class<? extends AuthenticationException>, Class<? extends AbstractAuthenticationFailureEvent>> mappings) {
        Assert.notEmpty(mappings, "The mappings Map must not be empty nor null");
        Iterator var2 = mappings.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<Class<? extends AuthenticationException>, Class<? extends AbstractAuthenticationFailureEvent>> entry = (Entry)var2.next();
            Class<?> exceptionClass = (Class)entry.getKey();
            Class<?> eventClass = (Class)entry.getValue();
            Assert.notNull(exceptionClass, "exceptionClass cannot be null");
            Assert.notNull(eventClass, "eventClass cannot be null");
            this.addMapping(exceptionClass.getName(), eventClass);
        }

    }
	
	// 设置默认身份验证失败事件
    public void setDefaultAuthenticationFailureEvent(Class<? extends AbstractAuthenticationFailureEvent> defaultAuthenticationFailureEventClass) {
        Assert.notNull(defaultAuthenticationFailureEventClass, "defaultAuthenticationFailureEventClass must not be null");

        try {
            this.defaultAuthenticationFailureEventConstructor = defaultAuthenticationFailureEventClass.getConstructor(Authentication.class, AuthenticationException.class);
        } catch (NoSuchMethodException var3) {
            throw new RuntimeException("Default Authentication Failure event class " + defaultAuthenticationFailureEventClass.getName() + " has no suitable constructor");
        }
    }
	
	// 注册事件  事件名称，事件类的class
    private void addMapping(String exceptionClass, Class<? extends AbstractAuthenticationFailureEvent> eventClass) {
        try {
            Constructor<? extends AbstractAuthenticationEvent> constructor = eventClass.getConstructor(Authentication.class, AuthenticationException.class);
			// 注册事件 异常映射
            this.exceptionMappings.put(exceptionClass, constructor);
        } catch (NoSuchMethodException var4) {
            throw new RuntimeException("Authentication event class " + eventClass.getName() + " has no suitable constructor");
        }
    }
}
