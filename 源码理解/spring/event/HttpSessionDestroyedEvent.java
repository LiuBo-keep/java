package org.springframework.security.web.session;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;

// httpsession销毁事件
public class HttpSessionDestroyedEvent extends SessionDestroyedEvent {
	// 构造器 初始化 事件源 HttpSession
    public HttpSessionDestroyedEvent(HttpSession session) {
        super(session);
    }
	
	// 获取session
    public HttpSession getSession() {
        return (HttpSession)this.getSource();
    }
	
	// 获取安全上下文
    public List<SecurityContext> getSecurityContexts() {
		// 获取当前session
        HttpSession session = this.getSession();
		// 获取当前session中的key
        Enumeration<String> attributes = session.getAttributeNames();
		// 创建上下文数组
        ArrayList contexts = new ArrayList();
		// 遍历
        while(attributes.hasMoreElements()) {
            String attributeName = (String)attributes.nextElement();
			// 通过key获取value
            Object attributeValue = session.getAttribute(attributeName);
			// 判断当前value是否是安全上下文的子类
			// 是则添加到上下文集合中
            if (attributeValue instanceof SecurityContext) {
                contexts.add((SecurityContext)attributeValue);
            }
        }

        return contexts;
    }
	
	// 获取sessionid
    public String getId() {
        return this.getSession().getId();
    }
}