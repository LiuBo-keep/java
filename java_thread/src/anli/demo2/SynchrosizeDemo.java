package anli.demo2;


/**
 * 两个线程对学生资源操作，一个线程设置属性，一个线程获取属性
 */
public class SynchrosizeDemo {

    public static void main(String[] args) {
        //资源类
        Student student=new Student();
        //设置线程任务
        SetRunnable setRunnable=new SetRunnable(student);
        //获取线程任务
        GetRunnable getRunnable=new GetRunnable(student);

        //将线程任务交给线程
        Thread t1=new Thread(setRunnable);
        Thread t2=new Thread(getRunnable);

        //启动
        t1.start();
        t2.start();
    }
}
