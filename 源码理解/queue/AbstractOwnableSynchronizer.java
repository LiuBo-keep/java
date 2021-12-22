/**
 * 设置 和 获取独占锁的拥有者线程
 *
 * 可以由线程以独占方式拥有的同步器。此类为创建锁和相关同步器（伴随着所有权的概念）提供了基础。
 * AbstractOwnableSynchronizer 类本身不管理或使用此信息。但是，子类和工具可以使用适当维护的值帮助控制和监视访问以及提供诊断。
 */

public abstract class AbstractOwnableSynchronizer
        implements java.io.Serializable {

    private static final long serialVersionUID = 3737899427754241961L;

    // 无参构造器
    protected AbstractOwnableSynchronizer() {
    }

    // 独占模式同步的当前所有者线程。
    // 互斥模式同步下的当前线程
    private transient Thread exclusiveOwnerThread;

    // 设置当前拥有独占访问的线程。锁的拥有线程，null 参数表示没有线程拥有访问。
    // 此方法不另外施加任何同步或 volatile 字段访问。
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    // 返回由 setExclusiveOwnerThread 最后设置的线程；
    // 如果从未设置，则返回 null。
    // 此方法不另外施加任何同步或 volatile 字段访问。
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}