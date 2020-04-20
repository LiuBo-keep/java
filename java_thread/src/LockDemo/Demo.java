package LockDemo;

public class Demo {
    public static void main(String[] args) {
        MyRunnable M=new MyRunnable();

        Thread t1=new Thread(M,"窗口1");
        Thread t2=new Thread(M,"窗口2");
        Thread t3=new Thread(M,"窗口3");

        t1.start();
        t2.start();
        t3.start();
    }
}
