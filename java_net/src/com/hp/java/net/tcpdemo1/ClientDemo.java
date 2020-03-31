package com.hp.java.net.tcpdemo1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * TCP协议发送数据
 * 1.创建发送端的Socket对象（这一步如果成功，就说明连接建立成功了）
 * 2.获取输出流，写数据
 * 3.释放资源
 */
public class ClientDemo {
    public static void main(String[] args)throws IOException {
        //创建发送端的Socket对象
        //方式1：Socket(InetAddress address,int port);
        //方式2：Socket(String host,int port);
        Socket socket=new Socket("127.0.0.1",8888);

        //获取输出流，写数据
        //public OutPutStream getOutPutStream();
        OutputStream outputStream=socket.getOutputStream();
        outputStream.write("hello_tcp".getBytes());

        //释放资源
        socket.close();
    }
}
