package com.hp.java.net.udpdemo;

import java.io.IOException;
import java.net.*;

/**
 *
 * UDP协议发送数据：
 * 1.创建发送端的socket对象
 *   DatagramSocket:此类表示用来发送和接受数据报包的套接字
 * 2.创建数据，并打包
 * 3.调用socket对象的方法发送数据包
 *    DatagramPacket:表示数据包：包含将要发送的数据，其长度，远程主机的IP，端口号
 * 4.释放资源
 */
public class Send
{
    public static void main(String[] args) throws IOException {
        //创建发送端的socket对象
        DatagramSocket ds=new DatagramSocket();

        //创建数据，并打包
        //DatagramPacket(byte[] buf,int length,InetAddress address,int port)
        //创建数据
        String msg="hello_UDPsocket";
        byte [] bytes=msg.getBytes();
        //长度
        int length=bytes.length;
        //IP地址对象
        InetAddress address=InetAddress.getByName("127.0.0.1");
        //端口
        int port=10086;
        DatagramPacket p=new DatagramPacket(bytes,length,address,port);

        //调用socket对象的方法发送数据包
        //public void send(DatagramPacket p)
        ds.send(p);

        //释放资源
        ds.close();
    }
}
