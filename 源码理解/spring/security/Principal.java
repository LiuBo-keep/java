package java.security;

import javax.security.auth.Subject;

// 该接口代表主体的抽象概念，可用于表示任何实体，例如个人、公司和登录 ID。
public interface Principal {

    // 将此主体与指定的对象进行比较。如果传入的对象与此接口的实现所表示的主体匹配，则返回 true。
    public boolean equals(Object another);

    public String toString();
	
    public int hashCode();

    // 返回主体名称
    public String getName();

    // 如果此主体隐含了指定的主题，则返回 true。
    public default boolean implies(Subject subject) {
        if (subject == null)
            return false;
        return subject.getPrincipals().contains(this);
    }
}
