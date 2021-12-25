package java.util;

/**
 * 应派生所有事件状态对象的根类。所有事件都是通过对对象的引用构建的，“源”在逻辑上被认为是所讨论的事件最初发生在其上的对象
 */

public class EventObject implements java.io.Serializable {

    private static final long serialVersionUID = 5516075349620653480L;

    /**
     * 最初发生事件的对象。
     */
    protected transient Object source;

    /**
     * 构造器初始化事件源
     */
    public EventObject(Object source) {
        if (source == null)
            throw new IllegalArgumentException("null source");

        this.source = source;
    }

    /**
     * 获取事件源
     */
    public Object getSource() {
        return source;
    }

    /**
     * 输出事件源
     */
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
