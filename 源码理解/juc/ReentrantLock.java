/**
 * 一个可重入互斥Lock具有与使用synchronized方法和语句访问的隐式监视锁相同的基本行为和语义，但具有扩展功能。
 * <p>
 * 什么是 “可重入”，可重入就是说某个线程已经获得某个锁，可以再次获取锁而不会出现死锁。
 * <p>
 * <p>
 * 该类的构造函数接受可选的公平参数。当设置true ，在争用下，锁有利于授予访问最长等待的线程。 否则，
 * 该锁不保证任何特定的访问顺序。 使用许多线程访问的公平锁的程序可能会比使用默认设置的整体吞吐量（即，
 * 更慢，通常要慢得多），但是具有更小的差异来获得锁定并保证缺乏饥饿。但是请注意，锁的公平性不能保证线
 * 程调度的公平性。 因此，使用公平锁的许多线程之一可以连续获得多次，而其他活动线程不进行而不是当前持有锁。
 * <p>
 * 建议的做法是始终立即跟随lock与try块的通话，最常见的是在之前/之后的建设，如：
 * class X {
 * private final ReentrantLock lock = new ReentrantLock();
 * // ...
 * public void m(){
 * lock.lock();
 * // block until condition holds
 * try {
 * // ... method body
 * } finally {
 * lock.unlock()
 * }
 * }
 * }
 * <p>
 * 除了实现Lock接口，这个类定义了许多public种protected方法用于检查锁的状态。 其中一些方法仅适用于仪器和监控。
 */

public class ReentrantLock implements Lock, java.io.Serializable {
    private static final long serialVersionUID = 7373984872572414699L;

    private final Sync sync;

    // 同步
    abstract static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -5179523762034025860L;

        abstract void lock();

        final boolean nonfairTryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }

        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

        private void readObject(java.io.ObjectInputStream s)
                throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    // 非公平同步锁
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    // 公平同步锁
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            acquire(1);
        }

        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }

    // 创建一个ReentrantLock的实例。
    public ReentrantLock() {
        sync = new NonfairSync();
    }

    // 根据给定的公平政策创建一个ReentrantLock的实例。
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

    // 获得锁。
    public void lock() {
        sync.lock();
    }

    // 获取锁定，除非当前线程是 interrupted 。
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    // 只有在调用时它不被另一个线程占用才能获取锁。
    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }

    // 如果在给定的等待时间内没有被另一个线程 占用 ，并且当前线程尚未被 保留，则获取该锁（ interrupted）
    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    // 尝试释放此锁。
    public void unlock() {
        sync.release(1);
    }

    // 返回Condition用于这种用途实例Lock实例。
    public Condition newCondition() {
        return sync.newCondition();
    }

    // 查询当前线程对此锁的暂停数量。
    public int getHoldCount() {
        return sync.getHoldCount();
    }

    // 查询此锁是否由当前线程持有。
    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }

    // 查询此锁是否由任何线程持有
    public boolean isLocked() {
        return sync.isLocked();
    }

    // 如果此锁的公平设置为true，则返回 true 。
    public final boolean isFair() {
        return sync instanceof FairSync;
    }

    // 返回当前拥有此锁的线程，如果不拥有，则返回 null 。
    protected Thread getOwner() {
        return sync.getOwner();
    }

    // 查询是否有线程正在等待获取此锁
    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    // 查询给定线程是否等待获取此锁。
    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    // 返回等待获取此锁的线程数的估计。
    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    // 返回包含可能正在等待获取此锁的线程的集合。
    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    // 查询任何线程是否等待与此锁相关联的给定条件。
    public boolean hasWaiters(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject) condition);
    }

    // 返回与此锁相关联的给定条件等待的线程数的估计。
    public int getWaitQueueLength(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject) condition);
    }

    // 返回包含可能在与此锁相关联的给定条件下等待的线程的集合。
    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject) condition);
    }

    // 返回一个标识此锁的字符串以及其锁定状态。
    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                "[Unlocked]" :
                "[Locked by thread " + o.getName() + "]");
    }
}
