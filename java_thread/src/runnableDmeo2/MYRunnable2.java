package runnableDmeo2;

//同步方法
public class MYRunnable2 implements Runnable{

    //定义100张票
    private static int tickets=100;


    @Override
    public void run() {

        method();
    }

    private synchronized void method() {
        while (true){
                if (tickets>0){
                    System.out.println(Thread.currentThread().getName()+"买票"+tickets--+"张");
                }
            }
        }
    }

