package com.hp.java.net.udpdemo;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * UDP协议接受数据：
 * 1.创建发送端的socket对象
 *   DatagramSocket:此类表示用来发送和接受数据报包的套接字
 * 2.创建数据包（接受容器）
 * 3.调用socket对象的方法接受数据包
 *     public void receive(DatagramPacket p)
 * 4.解析数据包，并显示在控组台
 * 5.释放资源
 */
public class Recv
{
    public static void main(String[] args) throws IOException {
        //创建发送端的socket对象
        //DatagramPacket(int port)
        DatagramSocket datagramSocket=new DatagramSocket(10086);

        //创建数据包（接受容器）
        //DatagramPacket(byte[] buf,int length)
        byte [] bytes=new byte[1024];
        int length=bytes.length;
        DatagramPacket packet=new DatagramPacket(bytes,length);

        //调用socket对象的方法接受数据包
        //public void receive(DatagramPacket p)
        datagramSocket.receive(packet);

        //解析数据包，并显示在控组台
        //获取对方ip
        //public InetAddress getAddress();
        InetAddress inetAddress=packet.getAddress();
        String ip=inetAddress.getHostAddress();
        //public byte[] getData();获取数据缓冲区
        //public int getLength();获取数据的实际长度
        byte [] bytes1=packet.getData();
        int len=packet.getLength();
        String msg= new String(bytes1,0,len);
        System.out.println(ip+"----->"+msg);

        //释放资源
        datagramSocket.close();
    }
}
