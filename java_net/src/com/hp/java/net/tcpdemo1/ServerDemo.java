package com.hp.java.net.tcpdemo1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP协议接受数据
 * 1.创建发送端的Socket对象
 * 2.监听客户端连接，返回一个对应的Socket对象
 * 3.获取输入流，读取数据显示控制台
 * 4.释放资源
 */
public class ServerDemo {
    public static void main(String[] args)throws IOException {
        //创建发送端的Socket对象
        //ServerSocket(int port)
        ServerSocket socket=new ServerSocket(8888);

        //监听客户端连接，返回一个对应的Socket对象
        //public Socket accept()
        Socket socket1=socket.accept();

        //获取输入流，读取数据显示控制台
        //InputStream getInputStream();
        InputStream stream=socket1.getInputStream();
        byte[] bytes=new byte[1024];
        int len=stream.read(bytes);
        String msg=new String(bytes,0,len);
        System.out.println(msg);

        //释放资源
        stream.close();
       // socket.close();服务器不能关闭
    }
}
