package demo3;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName NioTest3
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/26 0:28
 *
 *
 * 1.通道(Channel)：用于源节点与目标结点的连接。
 * 在Java NIO 中负责缓冲区中数据的传输。
 * Channel本身不存储数据，因此需要配合缓冲区进行传输。
 *
 * 2.通道的主要实现类
 *   java.nio.channels.Channel接口：
 *      |-- FileChannel
 *      |-- SocketChannel
 *      |-- ServerSocketChannel
 *      |-- DatagramChannel
 *
 * 3.获取通道
 *    - Java 针对支持通道的类提供了getChannel()方法
 *          本地IO：
 *          FileInputStream/FileOutputStream
 *          RandomAccessFile
 *
 *          网络IO：
 *          Socket
 *          ServerSocket
 *          DatagramSocket
 *
 *    - 在JDK 1.7中的NIO.2 针对各个通道提供了静态方法 open()
 *
 *    - 在JDK 1.7中的NIO.2 的Files 工具类的 newByteChannel()
 *
 * 4.通道之间的数据传输
 * transferFrom()
 * transferTo()
 */
public class NioTest3 {

    //1.利用通道完成文件的复制
    @Test
    public void fun1(){
        FileInputStream fis= null;
        FileOutputStream fos= null;

        //获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try{
            fis = new FileInputStream("1.jpg");
            fos = new FileOutputStream("2.jpg");

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //分配指定大小的缓冲区
            ByteBuffer allocate = ByteBuffer.allocate(1024);

            //将通道中的数据存入缓冲区中
            while (inChannel.read(allocate)!=-1){
                //切换读取数据的模式
                allocate.flip();
                //将缓冲区的数据写入通道中
                outChannel.write(allocate);
                //清空缓冲区
                allocate.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (outChannel != null){
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null){
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
