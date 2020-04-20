package runnableDmeo2;

/**
 * 线程任务类：
 * 实现Runnable接口
 * 重写run方法
 *
 * 注意：
 * 1.实现Runnable不能直接使用Thread中的方法
 * 2.可以使用一些Thread类中的一些静态方法（例如获取当前线程对象 Thread.currentThread()）
 */
public class MyRunnable1 implements Runnable{
    @Override
    public void run() {
        System.out.println("操作当前任务的线程："+Thread.currentThread().getName()+"哈哈");
    }
}
