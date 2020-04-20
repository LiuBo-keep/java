## Thread内的常用方法

```
1.获取线程名称：
public final String getName();
线程对象.getName();
2.设置多线名称
public final void setName(String name);
线程对象.setName("sss");
3.获取当前线程对象
public static Thread currentThread();
Thread.currentThread();
4.获取线程对象的优先级(线程默认优先级是5)
public final int getPriority();
线程对象.getPriority();
5.设置线程优先级(线程优先级范围：1-10)
public final void setPriority(int newPriority);
线程对象.setPriority(int newPriority);
6.线程休眠
public static void sleep(long millis);
Thread.sleep(long millis);
7.线程加入(等待该线程终止,可以让使用该方法的线程执行完毕，别的线程才可以开始)
public final void join();
线程对象.join();
8.线程礼让(暂停当前正在执行的线程对象，并执行其他线程，让多个线程执行更和谐，但不能保证一人一次)
public static void yield();
Thread.yield();
9.后台线程(将该线程标记为守护线程或用户线程，当正在运行的线程都是守护线程时，Java虚拟机退出。该方法必须在启动线程前调用。
如果三个线程，其中两个设置为守护线程，一个没有设置为守护线程，当没有设置为守护线程的线程执行完毕，则其他两个也停止)
public final void setDaemon(boolean on);
线程对象.setDaemon(boolean on);
10.中断线程
public final void stop();->以过时
线程对象.stop();
public void interrupt();
线程对象.interrupt(); 
```