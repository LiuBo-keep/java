package threadDemo1;

/**
 * 继承Thread类重写run方法
 * run方法内则是线程任务
 */
public class MyThread extends Thread{

    @Override
    public void run() {
        System.out.println("写作业");
    }
}
