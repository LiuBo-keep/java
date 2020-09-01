package Nio.baseBuffer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @ClassName Demo7
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/23 23:28
 */
public class Demo7 {
    /**
     * Scattering:将数据写入到Buffer时，可以采用buffer数组，依次写入【分散】
     * Gathering:从buffer读取数据时，可以采用buffer数组，依次读【聚集】
     */
    public static void main(String[] args) throws Exception {

        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        //创建端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定端口
        socketChannel.socket().bind(inetSocketAddress);

        //创建Buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接
        SocketChannel accept = socketChannel.accept();
        int messageLength = 8; //假定从客户端接受8个字节
        //循环读取
        while (true) {
            int byteRead = 0;
            while (byteRead < messageLength) {
                long l = accept.read(byteBuffers);
                byteRead += l;
                System.out.println("byteRead=" + byteRead);
                //使用流打印，查看position与limit
                Arrays.asList(byteBuffers).stream().map(byteBuffer -> byteBuffer.position() + "-------------" + byteBuffer.limit());
            }
            //将所有的buffer进行flip
            Arrays.asList(byteBuffers).forEach(byteBuffer -> byteBuffer.flip());

            //将数据读出显示到客户端
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long l = accept.write(byteBuffers);
                byteWrite += l;
            }

            //将所有的buffer进行clear
            Arrays.asList(byteBuffers).stream().forEach(byteBuffer -> byteBuffer.clear());

            System.out.println("byteRead=" + byteRead + "byteWrite=" + byteWrite + "messageLength" + messageLength);
        }
    }
}
