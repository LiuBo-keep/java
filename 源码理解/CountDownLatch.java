/**
 * countDownLatch这个类使一个线程等待其他线程各自执行完毕后再执行。
 *
 * 是通过一个计数器来实现的，计数器的初始值是线程的数量。每当一个线程执行完毕后，
 * 计数器的值就-1，当计数器的值为0时，表示所有线程都执行完毕，然后在闭锁上等待的线程就可以恢复工作了。
 *
 */

public class CountDownLatch {
   
    private static final class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 4982264981922014374L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

        protected int tryAcquireShared(int acquires) {
            return (getState() == 0) ? 1 : -1;
        }

        protected boolean tryReleaseShared(int releases) {
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;
                int nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;

    // 带参构造器(等待线程个数)
    public CountDownLatch(int count) {
        // 如果小于0 抛出不合法参数异常
        if (count < 0) throw new IllegalArgumentException("count < 0");

        this.sync = new Sync(count);
    }

    // 调用await()方法的线程会被挂起，它会等待直到count值为0才继续执行
    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    // 和await()类似，只不过等待一定的时间后count值还没变为0的话就会继续执行
    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    // 将count值减1
    public void countDown() {
        sync.releaseShared(1);
    }

    // 获取需要等待的数
    public long getCount() {
        return sync.getCount();
    }

    // toString方法
    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
