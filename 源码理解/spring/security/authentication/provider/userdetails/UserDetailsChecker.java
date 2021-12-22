package org.springframework.security.core.userdetails;

// 用户详细信息检查器
public interface UserDetailsChecker {
	// 检查
    void check(UserDetails toCheck);
}