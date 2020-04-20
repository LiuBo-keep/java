package anli.demo1;

public class MyThread extends Thread{

    public MyThread(String name){
        super(name);
    }

    //定义100张票
   private static int tickets=100;

    @Override
    public void run() {
        while (true){
            if (tickets>0){
                System.out.println(this.getName()+"买票"+tickets--+"张");
            }
        }
    }
}
