package org.springframework.security.provisioning;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.util.Assert;

// InMemoryUserDetailsManager是Spring Security 内置的实现UserDetailsManager 接口的默认配置实现。
// 它主要负责从配置文件中加载用户的账户信息
public class InMemoryUserDetailsManager implements UserDetailsManager, UserDetailsPasswordService {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, MutableUserDetails> users = new HashMap();
    private AuthenticationManager authenticationManager;

    public InMemoryUserDetailsManager() {
    }

    public InMemoryUserDetailsManager(Collection<UserDetails> users) {
        Iterator var2 = users.iterator();

        while(var2.hasNext()) {
            UserDetails user = (UserDetails)var2.next();
            this.createUser(user);
        }

    }

    public InMemoryUserDetailsManager(UserDetails... users) {
        UserDetails[] var2 = users;
        int var3 = users.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            UserDetails user = var2[var4];
            this.createUser(user);
        }

    }

    public InMemoryUserDetailsManager(Properties users) {
        Enumeration<?> names = users.propertyNames();
        UserAttributeEditor editor = new UserAttributeEditor();

        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            editor.setAsText(users.getProperty(name));
            UserAttribute attr = (UserAttribute)editor.getValue();
            this.createUser(this.createUserDetails(name, attr));
        }

    }

    private User createUserDetails(String name, UserAttribute attr) {
        return new User(name, attr.getPassword(), attr.isEnabled(), true, true, true, attr.getAuthorities());
    }

    public void createUser(UserDetails user) {
        Assert.isTrue(!this.userExists(user.getUsername()), "user should not exist");
        this.users.put(user.getUsername().toLowerCase(), new MutableUser(user));
    }

    public void deleteUser(String username) {
        this.users.remove(username.toLowerCase());
    }

    public void updateUser(UserDetails user) {
        Assert.isTrue(this.userExists(user.getUsername()), "user should exist");
        this.users.put(user.getUsername().toLowerCase(), new MutableUser(user));
    }

    public boolean userExists(String username) {
        return this.users.containsKey(username.toLowerCase());
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current user.");
        } else {
            String username = currentUser.getName();
            this.logger.debug(LogMessage.format("Changing password for user '%s'", username));
            if (this.authenticationManager != null) {
                this.logger.debug(LogMessage.format("Reauthenticating user '%s' for password change request.", username));
                this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
            } else {
                this.logger.debug("No authentication manager set. Password won't be re-checked.");
            }

            MutableUserDetails user = (MutableUserDetails)this.users.get(username);
            Assert.state(user != null, "Current user doesn't exist in database.");
            user.setPassword(newPassword);
        }
    }

    public UserDetails updatePassword(UserDetails user, String newPassword) {
        String username = user.getUsername();
        MutableUserDetails mutableUser = (MutableUserDetails)this.users.get(username.toLowerCase());
        mutableUser.setPassword(newPassword);
        return mutableUser;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = (UserDetails)this.users.get(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        }
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
