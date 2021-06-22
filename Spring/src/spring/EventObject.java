package spring;

/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 事件对象
 */
public class EventObject {

    /**
     * 最初发生事件的对象。
     */
    protected transient Object source;

    /**
     * 构建一个原型事件
     */
    public EventObject(Object source) {
        if (source == null)
            throw new IllegalArgumentException("null source");

        this.source = source;
    }

    /**
     * 最初发生事件的对象。
     */
    public Object getSource() {
        return source;
    }

    /**
     * 返回此 EventObject 的字符串表示形式。
     */
    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
