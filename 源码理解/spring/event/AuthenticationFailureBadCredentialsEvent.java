// 身份验证失败 Bad Credentials 事件
public class AuthenticationFailureBadCredentialsEvent extends AbstractAuthenticationFailureEvent {
	// 构造器 初始化 认证方式 异常信息
    public AuthenticationFailureBadCredentialsEvent(Authentication authentication, AuthenticationException exception) {
        super(authentication, exception);
    }
}