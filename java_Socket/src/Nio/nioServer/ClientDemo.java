package Nio.nioServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @ClassName ClientDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 14:43
 */
public class ClientDemo {
    public static void main(String[] args) throws Exception {
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //提供服务器IP 和端口
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        //连接服务器
        if (!socketChannel.connect(address)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("连接失败，客户端不会阻塞，可以做其他的事情");
            }
        }
        //连接成功
        String str = "学习Nio";
        //得到一个Buffer
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        //发送数据
        socketChannel.write(buffer);
        System.in.read();
    }
}
