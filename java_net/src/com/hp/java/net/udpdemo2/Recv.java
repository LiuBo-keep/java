package com.hp.java.net.udpdemo2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Recv
{
    private static boolean flag=true;

    public static void main(String[] args) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(10086);

        while (flag){
            byte [] bytes=new byte[1024];
            int length=bytes.length;
            DatagramPacket packet = new DatagramPacket(bytes, length);

            datagramSocket.receive(packet);

            InetAddress inetAddress = datagramSocket.getInetAddress();
            byte [] bytes1=packet.getData();
            int len=packet.getLength();
            String msg=new String(bytes1,0,len);

            if (!msg.equals(Integer.toString(886))){
//                System.out.println(inetAddress.getHostAddress()+"---->"+msg);
                System.out.println("---->"+msg);
            }else {
                datagramSocket.close();
                flag=false;
                System.out.println("结束通话");
            }
        }

    }
}
