package Nio多人聊天室;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName Server
 * @Description TODO
 * @Author 17126
 * @Date 2020/9/1 20:42
 */
public class Server {
    private ServerSocketChannel socketChannel = null;
    private Selector selector = null;
    private final int PORT = 6666;


    /**
     * 在构造器中初始化
     */
    public Server() {
        try {
            // ServerSocketChannel
            socketChannel = ServerSocketChannel.open();
            //等待选择器
            selector = Selector.open();
            //设置非阻塞模式
            socketChannel.configureBlocking(false);
            //绑定端口
            socketChannel.socket().bind(new InetSocketAddress(PORT));
            //将socketChannel注册到selector
            socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器启动");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听
     */
    private void listen() {
        try {
            while (true) {
                int select = selector.select();
                //有事件处理
                if (select > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> key = selectionKeys.iterator();
                    while (key.hasNext()) {
                        chat(key.next());
                        //当前的key 删除 防止重复处理
                        key.remove();
                    }
                } else {
                    System.out.println("等待连接....");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理事件
     */
    private void chat(SelectionKey keys) {
        try {
            //连接事件
            if (keys.isAcceptable()) {
                SocketChannel socket = this.socketChannel.accept();
                socket.configureBlocking(false);
                socket.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                //提示
                System.out.println(socket.getRemoteAddress() + "上线");
            }
            //读取事件
            if (keys.isReadable()) {
                //读取客户端消息
                SocketChannel channel = (SocketChannel) keys.channel();
                ByteBuffer buffer = (ByteBuffer) keys.attachment();
                int count = channel.read(buffer);
                //根据count的值做处理
                if (count > 0) {
                    //把缓冲区的数据转成字符串
                    String str = new String(buffer.array());
                    //输出消息
                    System.out.println("form 客户端" + str);
                    //向其他客户端转发消息(排除自己)
                    sendInfoToOtherClients(str, channel);

                }
            }
        } catch (IOException e) {
            try {
                SocketChannel channel = (SocketChannel) keys.channel();
                System.out.println(channel.getRemoteAddress() + "离线..");
                //取消注册
                keys.cancel();
                //关闭通道
                channel.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    /**
     * 向其他客户端转发消息(排除自己)
     */
    private void sendInfoToOtherClients(String msg, SocketChannel socketChannel) throws IOException {
        System.out.println("服务器转发消息中....");
        //遍历 所有注册到selector上的SocketChannel，并排除自己
        for (SelectionKey key : selector.keys()) {
            //通过key 取出对应的channel
            Channel channel = key.channel();
            //排除自己
            if (channel instanceof SocketChannel && channel != socketChannel) {
                //转型
                SocketChannel socket = (SocketChannel) channel;
                //将msg 存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer 的数据写入通道
                socket.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
