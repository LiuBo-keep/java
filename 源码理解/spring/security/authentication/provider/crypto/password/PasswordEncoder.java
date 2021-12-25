package org.springframework.security.crypto.password;

// 密码编码接口
public interface PasswordEncoder {
	// 编码
    String encode(CharSequence var1);
	
	// 密码对比是否一致
    boolean matches(CharSequence var1, String var2);
	
	// 升级编码
    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}