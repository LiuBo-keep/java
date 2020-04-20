package LockDemo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyRunnable implements Runnable{
    private Lock lock=new ReentrantLock();

    //定义100张票
    private static int tickets=100;

    @Override
    public void run() {
        while (true){
            lock.lock();
            try{
                if (tickets>0){
                    System.out.println(Thread.currentThread().getName()+"买票"+tickets--+"张");
                }
            }finally {
                lock.unlock();
            }
        }
    }
}
