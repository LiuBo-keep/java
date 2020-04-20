package anli.demo1;

/**
 * 电影院   3个窗口   售卖100张票，分别通过继承Thread类和实现Runnable接口两种方式实现
 */
public class ticketsDemo1 {
    public static void main(String[] args) {

        //继承Thread类
        //cread1();
        //实现Runnable接口
        cread2();
    }

    private static void cread2() {
        //创建任务类
        MyRunnable m=new MyRunnable();

        //将任务交给线程
        Thread t1=new Thread(m,"窗口1");
        Thread t2=new Thread(m,"窗口2");
        Thread t3=new Thread(m,"窗口3");

        //启动线程
        t1.start();
        t2.start();
        t3.start();
    }

    private static void cread1() {
        MyThread m1=new MyThread("窗口1");
        MyThread m2=new MyThread("窗口2");
        MyThread m3=new MyThread("窗口3");

        m1.start();
        m2.start();
        m3.start();
    }
}
