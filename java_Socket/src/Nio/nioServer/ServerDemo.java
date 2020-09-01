package Nio.nioServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName ServerDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 0:30
 */
public class ServerDemo {
    public static void main(String[] args) throws Exception {

        //创建ServerSocketChannel --> ServerSocket
        ServerSocketChannel socketChannel = ServerSocketChannel.open();

        //得到Selector对象
        Selector selector = Selector.open();

        //绑定一个端口，在服务端进行监听
        socketChannel.socket().bind(new InetSocketAddress(6666));

        //设置为非阻塞模式
        socketChannel.configureBlocking(false);

        //把Selector注册到Selector中关心事件为OP_ACCEPT
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {
            //没有事件发生（select(1000)表示等待时长，阻塞）
            if (selector.select(1000) == 0) {
                System.out.println("服务器等待1秒，无连接");
                continue;
            }

            //如果返回的>0，就获取到相关selectionKey集合
            //1.如果返回的>0，表示已经获取到关注的事件
            //2.selector.selectorKeys()返回关注事件的集合
            //通过selectionKey反向获取通道
            Set<SelectionKey> keys = selector.selectedKeys();

            //遍历Set<SelectionKey>,使用迭代器遍历
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                //获取到SelectionKey
                SelectionKey key = iterator.next();
                //根据key 对应的通道发生的事件做相应处理
                if (key.isAcceptable()) { //如果是OP_ACCEPT,有新的客户端连接
                    //该客户端生成一个SocketChannel
                    SocketChannel accept =socketChannel.accept();
                    //将SocketChannel设置为非阻塞
                    accept.configureBlocking(false);
                    //将socketChannel注册到selector，关注事件为OP_READ，同时给socketChannel关联一个Buffer
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if (key.isReadable()) {
                    //通过key反向得的对应的channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到该Channel关联的Buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    //获取通道中的信息
                    channel.read(buffer);
                    System.out.println("客户端：" + new String(buffer.array()));
                }
                //手动从集合中移除当前的selectionKet，反之重复操作
                iterator.remove();
            }
        }
    }
}
