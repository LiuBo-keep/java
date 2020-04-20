package synchrosizeDemo;

import runnableDmeo2.MYRunnable2;
import runnableDmeo2.MyRunnable1;

public class Demo {
    public static void main(String[] args) {

        //同步代码块
        /**
         *   - 格式： synchronized(对象){需要同步的代码（把多条语句操作共享数据的代码的部分给包起来）}
         *   - 同步可以解决安全问题的根本原因就是在那个对象上。（该对象如同锁的功能）
         *   - 注意：多个线程必须是同一把锁
         *
         *   同步的特点：
         *   - 同步的前提：
         *         1.多个线程
         *         2.多个线程使用的是同一把锁对象（对象可以是任意对象）
         *   - 同步的好处：
         *         1.同步的出现解决了多个线程的安全问题
         *   - 同步的弊端：
         *         1.当线程相当多时，因为每个线程都会去判断同步上的锁，这是很消耗资源的，无形中会降低程序的运行效率。
         */
       // fun1();
        //同步方法
        /**
         * - 格式：将synchronized加在方法上
         * - 同步方法的锁是this
         */
        //fun2();
        //静态同步方法
        /**
         *  - 格式：在带有synchronized的方法上加static
         *  - 同步方法的锁是类的字节码文件对象（class对象）加载时间
         */
        fun3();
    }

    private static void fun1() {
        //创建线程任务
        MyRunnable1 myRunnable=new MyRunnable1();

        Thread t1=new Thread(myRunnable,"窗口1");
        Thread t2=new Thread(myRunnable,"窗口2");
        Thread t3=new Thread(myRunnable,"窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

    private static void fun2() {
        //创建线程任务
        MYRunnable2 myRunnable=new MYRunnable2();

        Thread t1=new Thread(myRunnable,"窗口1");
        Thread t2=new Thread(myRunnable,"窗口2");
        Thread t3=new Thread(myRunnable,"窗口3");

        t1.start();
        t2.start();
        t3.start();
    }

    private static void fun3() {
    }
}
