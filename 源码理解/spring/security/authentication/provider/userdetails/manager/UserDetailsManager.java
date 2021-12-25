package org.springframework.security.provisioning;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

// 用户信息管理
public interface UserDetailsManager extends UserDetailsService {
    void createUser(UserDetails var1);

    void updateUser(UserDetails var1);

    void deleteUser(String var1);

    void changePassword(String var1, String var2);

    boolean userExists(String var1);
}