package spring.abstracts;

import spring.EventObject;


/**
 * @author LiuBo
 * @date 2021/6/22
 * @Description 描述
 */
public abstract class ApplicationEvent  extends EventObject {

    private final long timestamp = System.currentTimeMillis();

    public ApplicationEvent(Object source) {
        super(source);
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
