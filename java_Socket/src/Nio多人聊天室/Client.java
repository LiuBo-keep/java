package Nio多人聊天室;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @ClassName Client
 * @Description TODO
 * @Author 17126
 * @Date 2020/9/1 22:16
 */
public class Client {
    //定义相关属性
    private final String HOST = "127.0.0.1";
    private final int PORT = 6666;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    /**
     * 构造器初始化
     */
    public Client() throws IOException {
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //将channel注册到selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username（当前客户端）
        username = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username + "is ok ...");
    }

    /**
     * 向服务器发送消息
     */
    public void sendInfo(String msg) {
        msg = username + "说：" + msg;
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取从服务器端恢复的消息
     */
    public void readInfo() {
        try {
            int select = selector.select();
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        String s = new String(buffer.array());
                        System.out.println(s.trim());
                    }
                }
                iterator.remove();
            } else {
                System.out.println("没有可用的通道...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();

        new Thread(()->{
            while (true){
                client.readInfo();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //发数据给服务器
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            client.sendInfo(s

            );
        }
    }
}
