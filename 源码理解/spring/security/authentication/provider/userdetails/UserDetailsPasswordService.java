package org.springframework.security.core.userdetails;
// 用户信息密码服务
public interface UserDetailsPasswordService {
	// 修改密码
    UserDetails updatePassword(UserDetails var1, String var2);
}