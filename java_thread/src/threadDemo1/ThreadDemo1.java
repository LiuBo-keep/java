package threadDemo1;

/**
 * 继承Thread类实现多线程
 *
 * 继承Thread类实现多线程的不足：
 * 1.线程任务在Thread内，使用Thread类的同时实现重写run方法(耦合性太强)
 * 2.不能实现多个线程操作同一个线程任务
 * 3.同时也不推荐操作同一资源类
 */
public class ThreadDemo1 {
    public static void main(String[] args) {
        //创建线程类
       MyThread myThread=new MyThread();
       //获取线程名称(Thread类内的方法)
        System.out.println("线程的名称："+myThread.getName());
       //设置线程名称(Thread类内的方法)
        myThread.setName("线程1");
       //获取线程对象的优先级(Thread类内的方法)
        System.out.println("线程的优先级："+myThread.getPriority());
       //设置线程优先级(线程优先级范围：1-10) (Thread类内的方法)
        myThread.setPriority(6);
      //启动线程(Thread类内的方法)
        myThread.start();
    }
}
