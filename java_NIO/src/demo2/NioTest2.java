package demo2;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @ClassName NioTest2
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/25 23:53
 *
 *
 * 1.直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过allocate()方法分别缓冲区，将缓冲区建立在JVM的内存中
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中，可以提供效率
 */
public class NioTest2 {

    @Test
    public void fun(){

        //创建直接缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);

        //判断是否是直接缓冲区
        System.out.println(byteBuffer.isDirect());
    }
}
