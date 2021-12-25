package org.springframework.security.core.userdetails;

// 用户信息服务
// 在项目中可以实现此接口 实现自己的用户信息查询 
// spring Security 框架会自己调用此接口实现 进行获取用户信息(如果用户没有自己实现此接口则会使用框架自带的实现 CachingUserDetailsService，InMemoryUserDetailsManager 等等)
public interface UserDetailsService {
	// 更具用户名称加载用户信息
    UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
}