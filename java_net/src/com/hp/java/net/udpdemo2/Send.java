package com.hp.java.net.udpdemo2;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Send {

    private static boolean flag=true;

    public static void main(String[] args) throws IOException {
        //创建发送端的socket对象
        DatagramSocket ds=new DatagramSocket();

        while (flag){
            System.out.println("请输入的想说的：");
            Scanner scanner=new Scanner(System.in);
            String msg=scanner.next();
            if (!msg.equalsIgnoreCase(Integer.toString(886))){
                byte [] bytes=msg.getBytes();
                int len=bytes.length;
                InetAddress address=InetAddress.getByName("127.0.0.1");
                int port=10086;
                DatagramPacket p=new DatagramPacket(bytes,len,address,port);

                ds.send(p);
            }else {
                byte [] bytes=msg.getBytes();
                int len=bytes.length;
                InetAddress address=InetAddress.getByName("127.0.0.1");
                int port=10086;
                DatagramPacket p=new DatagramPacket(bytes,len,address,port);
                ds.send(p);
                ds.close();
                System.exit(0);
            }

        }
    }
}
