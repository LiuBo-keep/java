package Nio.baseBuffer;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName Demo3
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/20 23:23
 */
public class Demo3 {

    public static void main(String[] args) throws Exception{
        //创建文件出入流
        FileInputStream fileInputStream = new FileInputStream("a.txt");

        //获取相应的channel
        FileChannel channel = fileInputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将channel中的数据写入缓冲区
        channel.read(byteBuffer);

        //切换缓冲区的状态
        byteBuffer.flip();

        //将缓冲区中的数据进行输出
        System.out.println(new String(byteBuffer.array()));

        //释放资源
        fileInputStream.close();
    }
}
