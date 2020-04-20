package BlockingQueueDemo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingDemo1 {
    public static void main(String[] args) {
        BlockingQueue<String> blockingQueue=new ArrayBlockingQueue<String>(5);
        blockingQueue.add("aaa");
    }
}
