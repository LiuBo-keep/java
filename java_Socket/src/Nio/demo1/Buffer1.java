package Nio.demo1;

import java.nio.ByteBuffer;

/**
 * @ClassName Buffer1
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 22:25
 */
public class Buffer1 {
    public static void main(String[] args) throws Exception{
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        String str = "ABCDEFG";

        byteBuffer.put(str.getBytes());

        System.out.println("当前缓冲区的容量="+byteBuffer.capacity());
        System.out.println("当前最大可操作容量="+byteBuffer.limit());
        System.out.println("当前可操作位置="+byteBuffer.position());

        byteBuffer.flip();

        System.out.println("当前缓冲区的容量="+byteBuffer.capacity());
        System.out.println("当前最大可操作容量="+byteBuffer.limit());
        System.out.println("当前可操作位置="+byteBuffer.position());
        while (byteBuffer.hasRemaining()){
            System.out.println((char) byteBuffer.get());
        }
    }
}
