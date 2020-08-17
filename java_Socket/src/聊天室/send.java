package 聊天室;

/**
 * @ClassName send
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 22:29
 */
public class send {
    public static void main(String[] args) {
        //开启两个线程
        Thread thread1 = new Thread(new SendThread(7777, "127.0.0.1", 9999));
        Thread thread2 = new Thread(new reveoveThread(8888));

        thread1.start();
        thread2.start();
    }
}
