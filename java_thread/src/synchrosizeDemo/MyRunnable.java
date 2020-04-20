package synchrosizeDemo;

//同步代码块
public class MyRunnable implements Runnable{

    //定义100张票
    private static int tickets=100;
    //定义锁
    private static Object object=new Object();

    @Override
    public void run() {
        while (true){
           synchronized (object){
               if (tickets>0){
                   System.out.println(Thread.currentThread().getName()+"买票"+tickets--+"张");
               }
           }
        }
    }
}
