package anli.demo1;

public class MyRunnable implements Runnable{
    //定义100张票
    private static int tickets=100;

    @Override
    public void run() {
        while (true){
            if (tickets>0){
                System.out.println(Thread.currentThread().getName()+"买票"+tickets--+"张");
            }
        }
    }
}
