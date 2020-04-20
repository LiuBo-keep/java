package runnableDmeo2;

/**
 * 实现Runnable接口实现多线程
 *
 * 1.线程任务和线程是分开的(低耦合)
 * 2.可以很好的实现同一任务交给多个线程执行
 */
public class ThreadDemp2 {
    public static void main(String[] args) {
        //实现线程任务类
        MyRunnable1 myRunnable=new MyRunnable1();

        //创建线程，并将线程任务交给线程（多个线程执行一个线程任务）
        Thread t1=new Thread(myRunnable);
        Thread t2=new Thread(myRunnable);

        t1.start();
        t2.start();
    }
}
