package org.springframework.security.authentication;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

// 在SpringOauth2.0中，所有的认证服务，均通过ProviderManager认证管理中心进行认证。通过分析ProviderManager，
// 可以理解SpringOauth2.0认证的细节。也是整个流程中最核心的环节之一。
public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {
    private static final Log logger = LogFactory.getLog(ProviderManager.class);
	// 	认证事件发布者 用户授权成功或失败的通知机制
    private AuthenticationEventPublisher eventPublisher;
	// 身份验证提供程序集合(各种认证方式的集合)
    private List<AuthenticationProvider> providers;
	// 消息源访问器
    protected MessageSourceAccessor messages;
	// 认证管理器
    private AuthenticationManager parent;
	// 身份验证后擦除凭据
    private boolean eraseCredentialsAfterAuthentication;
	
	// 构造器 初始化 认证程序
    public ProviderManager(AuthenticationProvider... providers) {
        this(Arrays.asList(providers), (AuthenticationManager)null);
    }
	
	// 构造器 初始化 认证程序
    public ProviderManager(List<AuthenticationProvider> providers) {
        this(providers, (AuthenticationManager)null);
    }
	
	// 构造器 初始化 认证程序 认证管理器
    public ProviderManager(List<AuthenticationProvider> providers, AuthenticationManager parent) {
		// 事件推送
        this.eventPublisher = new ProviderManager.NullEventPublisher();
		// 认证方式集合
        this.providers = Collections.emptyList();
		// 消息源
        this.messages = SpringSecurityMessageSource.getAccessor();
		// 身份验证后擦除凭据 默认为true
        this.eraseCredentialsAfterAuthentication = true;
        Assert.notNull(providers, "providers list cannot be null");
		// 认证方式流程
        this.providers = providers;
		// 认证管理器
        this.parent = parent;
		// 检查状态
        this.checkState();
    }

    public void afterPropertiesSet() {
        this.checkState();
    }

    private void checkState() {
        Assert.isTrue(this.parent != null || !this.providers.isEmpty(), "A parent AuthenticationManager or a list of AuthenticationProviders is required");
        Assert.isTrue(!CollectionUtils.contains(this.providers.iterator(), (Object)null), "providers list cannot contain null values");
    }
	
	// 认证
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 获取认证的class对象
        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        AuthenticationException parentException = null;
        Authentication result = null;
        Authentication parentResult = null;
		// 当前位置
        int currentPosition = 0;
		// 获取认证流程的个数
        int size = this.providers.size();
		// 获取认证流程迭代器
        Iterator var9 = this.getProviders().iterator();
		// 对集合中的认证流程进行迭代
        while(var9.hasNext()) {
			// 获取认证流程
            AuthenticationProvider provider = (AuthenticationProvider)var9.next();
			// 判断当前认证流程和认证方式是否匹配
            if (provider.supports(toTest)) {
				// 日志是否启用跟踪
                if (logger.isTraceEnabled()) {
                    Log var10000 = logger;
                    String var10002 = provider.getClass().getSimpleName();
                    ++currentPosition;
                    var10000.trace(LogMessage.format("Authenticating request with %s (%d/%d)", var10002, currentPosition, size));
                }

                try {
					// 使用当前认证流程对当前认证方式进行认证
                    result = provider.authenticate(authentication);
					// 认证接口不为空
					// 将结果信息拷贝到authentication中，结束迭代
                    if (result != null) {
                        this.copyDetails(authentication, result);
                        break;
                    }
                } catch (InternalAuthenticationServiceException | AccountStatusException var14) {
                    this.prepareException(var14, authentication);
                    throw var14;
                } catch (AuthenticationException var15) {
                    lastException = var15;
                }
            }
        }
		// 结果为空并且认证管理器不为空
        if (result == null && this.parent != null) {
            try {
				// 进行认证
                parentResult = this.parent.authenticate(authentication);
                result = parentResult;
            } catch (ProviderNotFoundException var12) {
            } catch (AuthenticationException var13) {
                parentException = var13;
                lastException = var13;
            }
        }
		// 认证结果不为空
        if (result != null) {
			// 身份验证后擦除凭据是否为true 并且认证方式是否是CredentialsContainer子类
            if (this.eraseCredentialsAfterAuthentication && result instanceof CredentialsContainer) {
				// 凭证擦除
                ((CredentialsContainer)result).eraseCredentials();
            }
			// parentResult为空
            if (parentResult == null) {
				// 发布认证成功事件
                this.eventPublisher.publishAuthenticationSuccess(result);
            }
			// 返回结果
            return result;
        } else {
            if (lastException == null) {
                lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound", new Object[]{toTest.getName()}, "No AuthenticationProvider found for {0}"));
            }

            if (parentException == null) {
                this.prepareException((AuthenticationException)lastException, authentication);
            }

            throw lastException;
        }
    }
	
	// 准备异常
    private void prepareException(AuthenticationException ex, Authentication auth) {
		// 发布认证失败事件
        this.eventPublisher.publishAuthenticationFailure(ex, auth);
    }

    private void copyDetails(Authentication source, Authentication dest) {
        if (dest instanceof AbstractAuthenticationToken && dest.getDetails() == null) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken)dest;
            token.setDetails(source.getDetails());
        }

    }

    public List<AuthenticationProvider> getProviders() {
        return this.providers;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
        Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
        this.eventPublisher = eventPublisher;
    }

    public void setEraseCredentialsAfterAuthentication(boolean eraseSecretData) {
        this.eraseCredentialsAfterAuthentication = eraseSecretData;
    }

    public boolean isEraseCredentialsAfterAuthentication() {
        return this.eraseCredentialsAfterAuthentication;
    }

    private static final class NullEventPublisher implements AuthenticationEventPublisher {
        private NullEventPublisher() {
        }

        public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        }

        public void publishAuthenticationSuccess(Authentication authentication) {
        }
    }
}
