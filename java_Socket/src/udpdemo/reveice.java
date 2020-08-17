package udpdemo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @ClassName reveice
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 21:48
 */
public class reveice {

    public static void main(String[] args) throws Exception {
        //建立发送端端口
        DatagramSocket socket = new DatagramSocket(8002);
        //建立数据容器
        byte[] buffer = new byte[1024];
        //建立接收数据的包
        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
        //接收数据包
        socket.receive(packet);
        //输出内容
        System.out.print(packet.getAddress()+":");
        System.out.println(new String(packet.getData()));
    }
}
