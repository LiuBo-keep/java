package org.springframework.security.web.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

// 用户名密码认证过滤器
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	// 用户名
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	// 密码
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	// 默认路径请求匹配器
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login", "POST");
	// 用户名参数
    private String usernameParameter = "username";
	// 密码参数
    private String passwordParameter = "password";
	// 仅post
    private boolean postOnly = true;
	
	// 构造器初始化 默认请求路径匹配器
    public UsernamePasswordAuthenticationFilter() {
		// 初始化 父类
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }
	// 构造器 初始化 认证管理器
    public UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
		// 初始化 父类
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }
	
	// 尝试认证
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		// 认证请求方式只可以是post方式
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
			// 从HttpServletRequest获取用户名
            String username = this.obtainUsername(request);
            username = username != null ? username : "";
            username = username.trim();
			// 从HttpServletRequest获取密码
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
			// 创建UsernamePasswordAuthenticationToken认证方式 
			// public UsernamePasswordAuthenticationToken(Object principal, Object credentials) {
            // this.principal = principal;
            // this.credentials = credentials;
            // this.setAuthenticated(false);
            // }
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
			// 设置详情 请求request  认证方式
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
	
	// 从请求中获取密码
    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }
	
	// 从请求中获取用户名
    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }
	
	// 设置认证详情
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		// 初始化AbstractAuthenticationToken 中的details属性
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getUsernameParameter() {
        return this.usernameParameter;
    }

    public final String getPasswordParameter() {
        return this.passwordParameter;
    }
}
