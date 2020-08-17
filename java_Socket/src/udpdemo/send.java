package udpdemo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @ClassName send
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 21:47
 */
public class send {

    public static void main(String[] args) throws Exception {
        //建立发送端端口
        DatagramSocket socket = new DatagramSocket(8001);
        //目标IP
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        //目标端口
        int port = 8002;
        //建立数据
        String msg = "我是发送端";
        //建立发送数据包
        DatagramPacket packet = new DatagramPacket(msg.getBytes(),0,msg.getBytes().length,inetAddress,port);
        //发送数据包
        socket.send(packet);
    }
}
