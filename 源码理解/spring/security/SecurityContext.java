package org.springframework.security.core.context;

import java.io.Serializable;
import org.springframework.security.core.Authentication;


// 安全上下文对象
public interface SecurityContext extends Serializable {
	
	// 获取认证成功后的信息
    Authentication getAuthentication();
	
	// 设置认证成功后的信息
    void setAuthentication(Authentication var1);
}
