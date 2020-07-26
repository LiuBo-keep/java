package demo1;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @ClassName NioTest
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/25 22:47
 *
 *
 * 1.缓冲区(Buffer)：在Java NIO 中负责数据的存储。
 * 缓冲区就是数组，用于存储不同数据类型的数据
 *
 * 根据数据类型不同(boolean 除外)，提供了相应类型的缓冲区
 *
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloutBuffer
 * DoubleBuffer
 *
 *
 * 上述缓冲区的管理方式几乎一致，通过allocate() 获取缓冲区
 *
 *
 * 2.缓冲区存取数据的两个核心方法
 * put()：存入数据到缓冲区
 * get()：获取缓冲区中的数据
 *
 *
 * 3.缓冲区中的四个核心属性
 *     //标记，表示记录当前position的位置，可以通过reset()恢复到mark的位置
 *     private int mark = -1;
 *     //位置,表示缓冲区中正在操作数据的位置
 *     private int position = 0;
 *     //界限，表示缓冲区中可以操作数据的大小(limit后数据不能进行读写)
 *     private int limit;
 *     //容量，表示缓冲区最大存储数据的容量，一旦声明不能改变
 *     private int capacity;
 *
 *    0 < mark < position < limit < capacity
 */
public class NioTest {

    @Test
    public void test1(){
        //定义数据
        String str="abcde";

        //获取缓冲区并指定缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        System.out.println("----------------allocate----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //向缓冲区添加数据
        byteBuffer.put(str.getBytes());

        System.out.println("----------------put----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //读取数据需要切换模式
        byteBuffer.flip();

        System.out.println("----------------flip----------------------");
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        System.out.println("----------------get----------------------");
        //读取缓冲区中的数据
        byte [] aByte = new byte[byteBuffer.limit()];
        byteBuffer.get(aByte);

        System.out.println(new String(aByte));

        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());

        //rewind():可以重复读取数据
        System.out.println("----------------rewind----------------------");
        byteBuffer.rewind();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());


        //clear()：清空缓冲区,但是缓冲区中的数据依然存在，但是处于“被遗忘”状态
        System.out.println("----------------clear----------------------");
        byteBuffer.clear();
        System.out.println(byteBuffer.position());
        System.out.println(byteBuffer.limit());
        System.out.println(byteBuffer.capacity());
    }


    @Test
    public void test2(){
        //定义数据
        String str="abcde";

        ByteBuffer allocate = ByteBuffer.allocate(1024);

        allocate.put(str.getBytes());

        allocate.flip();

        byte[] bytes = new byte[2];

        allocate.get(bytes, 0, 2);
        System.out.println(new String(bytes));
        System.out.println(allocate.position());

        //标记：当前position的位置
        allocate.mark();

        allocate.get(bytes, 0, 2);
        System.out.println(new String(bytes));
        System.out.println(allocate.position());

        //reset()；恢复到mark的位置
        allocate.reset();
        System.out.println(allocate.position());
    }
}
