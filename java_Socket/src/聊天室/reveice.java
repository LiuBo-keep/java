package 聊天室;

/**
 * @ClassName reveice
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 22:29
 */
public class reveice {
    public static void main(String[] args) {
        //开启两个线程
        Thread thread1 = new Thread(new SendThread(5555, "127.0.0.1", 8888));
        Thread thread2 = new Thread(new reveoveThread(9999));


        thread1.start();
        thread2.start();
    }
}
