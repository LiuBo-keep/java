// 密码编码器
public interface PasswordEncoder {
	// 编码
    String encode(CharSequence rawPassword);
	// 比较两个密码是否一致
    boolean matches(CharSequence rawPassword, String encodedPassword);
	// 升级编码
    default boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}