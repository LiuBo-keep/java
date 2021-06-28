import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class Thread implements Runnable {

    // ****************************成员变量*******************************

    static {
        registerNatives();
    }

    private static native void registerNatives();

    // 线程名称
    private volatile String name;
    // 线程优先级
    private int priority;
    // 线程可以拥有的最低优先级。
    public final static int MIN_PRIORITY = 1;
    // 分配给线程的默认优先级。
    public final static int NORM_PRIORITY = 5;
    // 线程可以拥有的最大优先级。
    public final static int MAX_PRIORITY = 10;
    // 线程
    private java.lang.Thread threadQ;
    // 线程是否为守护线程。
    private boolean daemon = false;
    // JVM状态
    private boolean stillborn = false;
    // 线程任务
    private Runnable target;
    // 线程组
    private ThreadGroup group;
    // 当前线程上下文类加载器
    private ClassLoader contextClassLoader;
    // 线程初始化数量
    private static int threadInitNumber;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    // 堆栈大小
    private long stackSize;
    // 线程id
    private long tid;
    // 用于生成线程id
    private static long threadSeqNumber;
    // 线程状态
    private volatile int threadStatus = 0;

    private static synchronized long nextThreadID() {
        return ++threadSeqNumber;
    }

    // 与当前线程绑定的信息
    ThreadLocal.ThreadLocalMap threadLocals = null;
    // 可继承的线程局部变量
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    private long eetop;
    private boolean single_step;
    private AccessControlContext inheritedAccessControlContext;
    private long nativeParkEventPointer;
    volatile Object parkBlocker;
    private volatile Interruptible blocker;
    private final Object blockerLock = new Object();

    void blockedOn(Interruptible b) {
        synchronized (blockerLock) {
            blocker = b;
        }
    }

    // ****************************构造器*******************************

    // 无参构造器
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }

    // 带参数构造器(线程任务)
    public Thread(Runnable target) {
        init(null, target, "Thread-" + nextThreadNum(), 0);
    }

    // 带参数构造器(线程组,线程任务)
    public Thread(ThreadGroup group, Runnable target) {
        init(group, target, "Thread-" + nextThreadNum(), 0);
    }

    // 带参数构造器(线名称)
    public Thread(String name) {
        init(null, null, name, 0);
    }

    // 带参数构造器(线程任务，访问控制上下文)
    Thread(Runnable target, AccessControlContext acc) {
        init(null, target, "Thread-" + nextThreadNum(), 0, acc, false);
    }

    // 带参数构造器(线程组，线程名称)
    public Thread(ThreadGroup group, String name) {
        init(group, null, name, 0);
    }

    // 带参数构造器(线程任务，线程名称)
    public Thread(Runnable target, String name) {
        init(null, target, name, 0);
    }

    // 带参数构造器(线程组，线程任务，线程名称)
    public Thread(ThreadGroup group, Runnable target, String name) {
        init(group, target, name, 0);
    }

    // 带参数构造器(线程组，线程任务，线程名称，堆栈大小)
    public Thread(ThreadGroup group, Runnable target, String name,
                  long stackSize) {
        init(group, target, name, stackSize);
    }

    // ****************************方法*******************************

    // 返回对当前正在执行的线程对象的引用。(Thread.currentThread返回当前线程)
    public static native java.lang.Thread currentThread();

    // 线程礼让(暂停当前正在执行的线程对象，并执行其他线程，让多个线程执行更和谐，但不能保证一人一次)
    public static native void yield();

    // 线程休眠
    public static native void sleep(long millis) throws InterruptedException;

    public static void sleep(long millis, int nanos)
            throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        sleep(millis);
    }

    // 初始化当前线程
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null, true);
    }

    // 初始化当前线程(线程组，线程任务，线程名称，堆栈大小，，)
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        // 线程名称为null
        if (name == null) {
            // 抛出空指针异常
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;
        // 获取当前线程
        java.lang.Thread parent = currentThread();
        // 获取系统安全管理器
        SecurityManager security = System.getSecurityManager();
        // 线程组为null
        if (g == null) {
            // 系统安全管理器不为空
            if (security != null) {
                // 获取系统管理器的线程组赋值给当前线程
                g = security.getThreadGroup();
            }
            // 线程组为null
            if (g == null) {
                // 获取父级线程组赋值给当前线程
                g = parent.getThreadGroup();
            }
        }
        // 线程组检查使用权
        g.checkAccess();
        // 系统安全管理器不为null
        if (security != null) {

            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }
        // 线程组添加未启动
        g.addUnstarted();
        // 线程组
        this.group = g;
        // 是否是守护线程
        this.daemon = parent.isDaemon();
        // 线程优先级
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        // 线程任务
        this.target = target;
        // 设置线程优先级
        setPriority(priority);
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                    ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        // 堆栈大小
        this.stackSize = stackSize;

        // 线程id
        tid = nextThreadID();
    }

    // 抛出 CloneNotSupportedException 作为线程不能被有意义地克隆。而是构造一个新线程。
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    // 启动当前线程(线程处于就绪状态)
    public synchronized void start() {
        // 线程状态不为0时(默认当前线程没启动时，线程状态为0)
        if (threadStatus != 0)
            // 抛出不合法线程状态异常
            throw new IllegalThreadStateException();

        // 将当前线程添加到线程组中
        group.add(this);
        // 开始状态为false(当前线程还没有就绪)
        boolean started = false;
        try {
            start0();
            // 开启状态为true(当前线程已经就绪)
            started = true;
        } finally {
            try {
                // 当前线程没有就绪时
                if (!started) {
                    // 告诉线程组当前线程启动失败,从线程组中移除当前启动失败线程
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {

            }
        }
    }

    // 启动当前线程(本地方法库中方法)
    private native void start0();

    // 线程任务
    @Override
    public void run() {
        // 当前线程任务不为null
        if (target != null) {
            // 调用run方法
            target.run();
        }
    }

    // 系统调用此方法以在当前线程实际退出之前进行清理。
    private void exit() {
        // 线程组不为null
        if (group != null) {
            // 从线程组中移除当前线程
            group.threadTerminated(this);
            // 线程组设置为null
            group = null;
        }
        // 线程任务设置为null
        target = null;
        // 与当前线程绑定的信息设置为null
        threadLocals = null;
        // 与当前线程可继承的线程局部变量设置为null
        inheritableThreadLocals = null;
        inheritedAccessControlContext = null;
        blocker = null;
        uncaughtExceptionHandler = null;
    }

    // 中断当前线程线程(停止当前线程，此方法过时)
    @Deprecated
    public final void stop() {
        // 获取系统安全管理器
        SecurityManager security = System.getSecurityManager();
        // 系统安全管理器不为null
        if (security != null) {
            // 检查使用权
            checkAccess();
            // 当前对象不等于当前线程时
            if (this != java.lang.Thread.currentThread()) {
                // 检查是否允许当前线程停止
                security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
            }
        }
        // 当前线程状态不为0
        if (threadStatus != 0) {
            // 如果线程被挂起，则唤醒线程；否则无操作
            resume();
        }
        // JVM 可以处理所有线程状态
        stop0(new ThreadDeath());
    }

    @Deprecated
    public final synchronized void stop(Throwable obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * 每个线程都一个状态位用于标识当前线程对象是否是中断状态。
     * isInterrupted是一个实例方法，主要用于判断当前线程对象的中断标志位是否被标记了，
     * 如果被标记了则返回true表示当前已经被中断，否则返回false。
     * https://www.cnblogs.com/yangming1996/p/7612653.html
     * <p>
     * 如果一个线程处于了阻塞状态（如线程调用了thread.sleep、thread.join、thread.wait、
     * 1.5中的condition.await、以及可中断的通道上的 I/O 操作方法后可进入阻塞状态），则在
     * 线程在检查中断标示时如果发现中断标示为true，则会在这些阻塞方法（sleep、join、wait、
     * 1.5中的condition.await及可中断的通道上的 I/O 操作方法）调用处抛出InterruptedException
     * 异常，并且在抛出异常后立即将线程的中断标示位清除，即重新设置为false。抛出异常是为了
     * 线程从阻塞状态醒过来，并在结束线程前让程序员有足够的时间来处理中断请求。
     **/

    // 当前线程中断
    public void interrupt() {
        if (this != java.lang.Thread.currentThread())
            checkAccess();

        synchronized (blockerLock) {
            Interruptible b = blocker;
            if (b != null) {
                interrupt0();
                b.interrupt(this);
                return;
            }
        }
        interrupt0();
    }

    // 判断当前线程是否被中断(不会重置当前线程中断标志位)
    public boolean isInterrupted() {
        return isInterrupted(false);
    }

    // 判断当前线程是否被中断(重置当前线程中断状态，从中断改为正常)
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }

    private native boolean isInterrupted(boolean ClearInterrupted);

    // 设置线程优先级
    public final void setPriority(int newPriority) {
        // 线程组 g
        ThreadGroup g;
        // 检查权限
        checkAccess();
        // 如果设置的线程优先级大于最大或者小于最小优先级
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            // 抛出非法参数异常
            throw new IllegalArgumentException();
        }
        // 获取当前线程的线程组并且不为null
        if ((g = getThreadGroup()) != null) {
            // 设置线程优先级大于线程组的最大优先级
            if (newPriority > g.getMaxPriority()) {
                // 新的线程优先级等于线程组最大优先级
                newPriority = g.getMaxPriority();
            }
            // 设置优先级
            setPriority0(priority = newPriority);
        }
    }

    private native void setPriority0(int newPriority);

    // 获取当前线程优先级
    public final int getPriority() {
        return priority;
    }

    // 设置线程的名称
    public final synchronized void setName(String name) {
        // 检查权限
        checkAccess();
        // 如果设置的名称为null
        if (name == null) {
            // 抛出空指针异常
            throw new NullPointerException("name cannot be null");
        }
        // 线程名称
        this.name = name;
        // 线程状态不为0时，即线程处于活动状态
        if (threadStatus != 0) {
            // 设置线程名称
            setNativeName(name);
        }
    }

    private native void setNativeName(String name);

    // 获取线程名称
    public final String getName() {
        return name;
    }

    // 获取线程所在线程组
    public final ThreadGroup getThreadGroup() {
        return group;
    }

    // 返回当前线程组即线程子组的活动数
    public static int activeCount() {
        return currentThread().getThreadGroup().activeCount();
    }

    // 线程加入
    public final synchronized void join(long millis)
            throws InterruptedException {
        // 获取当前系统时间
        long base = System.currentTimeMillis();
        // now为0
        long now = 0;
        // 判断设置毫秒值小于0
        if (millis < 0) {
            // 抛出不合法参数异常
            throw new IllegalArgumentException("timeout value is negative");
        }
        // 如果设置毫秒值等于0
        if (millis == 0) {
            //  如果当前线程处于活动状态
            while (isAlive()) {
                // 阻塞当前线程(则不考虑实际时间，在获得通知前该线程将一直等待)
                wait(0);
            }
            // 如果设置毫秒值大于0
        } else {
            // 如果当前线程处于活动状态
            while (isAlive()) {
                // 设置毫秒值- now
                long delay = millis - now;
                // 如果小于等于0
                if (delay <= 0) {
                    // 跳出
                    break;
                }
                // 当前线程阻塞delay毫秒
                wait(delay);
                // 获取当前系统时间- 开始时间
                now = System.currentTimeMillis() - base;
            }
        }
    }

    // 线程加入
    public final synchronized void join(long millis, int nanos)
            throws InterruptedException {
        // 判断设置毫秒值小于0
        if (millis < 0) {
            // 抛出不合法参数异常
            throw new IllegalArgumentException("timeout value is negative");
        }
        // 如果纳米小于或者大于99999
        if (nanos < 0 || nanos > 999999) {
            // 抛出不合法参数异常
            throw new IllegalArgumentException(
                    "nanosecond timeout value out of range");
        }
        // 如果纳米大于等于500000 或者纳米不为0并且毫秒==0
        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            // 毫秒加1
            millis++;
        }
        // 线程加入方法
        join(millis);
    }

    // 线程加入
    public final void join() throws InterruptedException {
        join(0);
    }

    // 将线程设置为守护线程
    public final void setDaemon(boolean on) {
        // 检查权限
        checkAccess();
        if (isAlive()) {
            throw new IllegalThreadStateException();
        }
        daemon = on;
    }

    // 判断当前线程是否是守护线程
    public final boolean isDaemon() {
        return daemon;
    }

    // 确定当前运行的线程是否有权修改此线程。
    public final void checkAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkAccess(this);
        }

    }

    // 打印当前线程信息
    public String toString() {
        ThreadGroup group = getThreadGroup();
        if (group != null) {
            return "Thread[" + getName() + "," + getPriority() + "," +
                    group.getName() + "]";
        } else {
            return "Thread[" + getName() + "," + getPriority() + "," +
                    "" + "]";
        }
    }

    public ClassLoader getContextClassLoader() {
        if (contextClassLoader == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader.checkClassLoaderPermission(contextClassLoader,
                    Reflection.getCallerClass());
        }
        return contextClassLoader;
    }

    public void setContextClassLoader(ClassLoader cl) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setContextClassLoader"));
        }
        contextClassLoader = cl;
    }

    public static native boolean holdsLock(Object obj);

    // 堆栈跟踪中的一个元素，由Throwable.getStackTrace()返回。
    // 每个元素表示单个堆栈帧。 堆栈顶部除堆栈之外的所有堆栈都表示方法调用。
    // 堆栈顶部的帧表示生成堆栈跟踪的执行点。 通常，这是创建与堆栈跟踪相对应的throwable的点。
    private static final StackTraceElement[] EMPTY_STACK_TRACE
            = new StackTraceElement[0];

    // 返回表示此线程的堆栈转储的堆栈跟踪元素数组。 该方法将返回一个零长度的数组，
    // 如果该线程尚未启动，已启动但尚未被计划运行，或已终止。 如果返回的数组非零长度，
    // 则数组的第一个元素表示堆栈的顶部，这是序列中最近的方法调用。 数组的最后一个元
    // 素表示堆栈的底部，这是序列中最近最少的方法调用。
    public StackTraceElement[] getStackTrace() {
        if (this != java.lang.Thread.currentThread()) {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(
                        SecurityConstants.GET_STACK_TRACE_PERMISSION);
            }
            if (!isAlive()) {
                return EMPTY_STACK_TRACE;
            }
            StackTraceElement[][] stackTraceArray = dumpThreads(new java.lang.Thread[]{this});
            StackTraceElement[] stackTrace = stackTraceArray[0];
            if (stackTrace == null) {
                stackTrace = EMPTY_STACK_TRACE;
            }
            return stackTrace;
        } else {
            return (new Exception()).getStackTrace();
        }
    }

    // 返回所有活动线程的堆栈跟踪图。 地图键是线程，每个地图值是StackTraceElement数组，
    // 表示对应的Thread的堆栈转储。 返回的堆栈跟踪格式为getStackTrace方法指定的格式。
    // 线程可能正在执行，而此方法被调用。 每个线程的堆栈跟踪仅表示快照，并且可以在不同
    // 时间获取每个堆栈跟踪。 如果虚拟机没有关于线程的堆栈跟踪信息，则将在地图值中返回零长度的数组。
    public static Map<java.lang.Thread, StackTraceElement[]> getAllStackTraces() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(
                    SecurityConstants.GET_STACK_TRACE_PERMISSION);
            security.checkPermission(
                    SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
        }
        java.lang.Thread[] threads = getThreads();
        StackTraceElement[][] traces = dumpThreads(threads);
        Map<java.lang.Thread, StackTraceElement[]> m = new HashMap<>(threads.length);
        for (int i = 0; i < threads.length; i++) {
            StackTraceElement[] stackTrace = traces[i];
            if (stackTrace != null) {
                m.put(threads[i], stackTrace);
            }
        }
        return m;
    }

    // 获取线程id
    public long getId() {
        return tid;
    }

    // 线程状态
    public enum State {
        // 尚未启动的线程的线程状态。
        NEW,

        // 可运行线程的线程状态。处于可运行状态的线程正在 Java 虚拟机中执行，
        // 但它可能正在等待来自操作系统的其他资源，例如处理器。
        RUNNABLE,

        // 线程阻塞等待监视器锁的线程状态。处于阻塞状态的线程正在等待监视器锁
        // 进入同步块/方法或在调用{@link Object#wait() Object.wait} 后重新进入同步块/方法。
        BLOCKED,

        // 等待线程的线程状态。由于调用以下方法之一，线程处于等待状态：
        WAITING,

        // 具有指定等待时间的等待线程的线程状态。由于调用以下方法之一并具
        // 有指定的正等待时间，线程处于定时等待状态
        TIMED_WAITING,

        // 终止线程的线程状态。线程已完成执行。
        TERMINATED;
    }

    // 获取线程当前状态
    public java.lang.Thread.State getState() {
        return sun.misc.VM.toThreadState(threadStatus);
    }

}
