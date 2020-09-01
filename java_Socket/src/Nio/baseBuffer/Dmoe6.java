package Nio.baseBuffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName Dmoe6
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/23 21:02
 */
public class Dmoe6 {

    //MappedByteBuffer 可以让文件直接在内存(堆外内存)修改，操作系统不需要拷贝一次
    public static void main(String[] args) throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("a.txt", "rw");

        //获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();

        /*
         *参数1：FileChannel.MapMode.READ_WRITE 使用的读写魔兽
         * 参数2： 0，可以映射修改的起始位置
         * 参数3： 5，是映射到内存的大小，即将a.txt的多少字节映射到内存可以直接修改的范围就是0-5
         *
        */
        MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        //修改第一个为W
        byteBuffer.put(0,(byte)'W');
        //修改第5个为L(下标从0开始)
        byteBuffer.put(4,(byte) 'L');

        randomAccessFile.close();
        System.out.println("修改完成");
    }
}
