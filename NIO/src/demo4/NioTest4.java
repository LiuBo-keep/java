package demo4;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioTest4
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/26 22:04
 *
 * 1.分散(Scatter)与聚集(Gather)
 * 分散读取(Scatter Reads)：将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gathering Writes)：将多个缓冲区中的数据聚集到通道中
 */
public class NioTest4 {



    @Test
    public void fun1() throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile("1.txt", "rw");

        //分散读取

        //获取通道
        FileChannel channel = accessFile.getChannel();

        //分配指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(100);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);

        //分散读取
        ByteBuffer [] bufs = {buffer1,buffer2};
        channel.read(bufs);

        for (ByteBuffer b :
                bufs) {
            b.flip();
        }

        System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
        System.out.println("--------------------------------");
        System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));


        //聚集写入
        RandomAccessFile accessFile2 = new RandomAccessFile("2.txt", "rw");
        FileChannel channel1 = accessFile2.getChannel();

        channel1.write(bufs);
    }

}
