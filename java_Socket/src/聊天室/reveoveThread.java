package 聊天室;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * @ClassName reveoveThread
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 22:21
 */
public class reveoveThread implements Runnable{

    DatagramSocket socket = null;
    private int fromPort;

    public reveoveThread(int fromPort) {
        this.fromPort = fromPort;
        try {
            socket = new DatagramSocket(fromPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true){
                //建立数据容器
                byte[] buffer = new byte[1024];
                //建立接收数据的包
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                //接收数据包
                socket.receive(packet);

                //取出数据
                byte[] data = packet.getData();
                String msg = new String(data, 0, data.length);

                System.out.println("发送者端口："+packet.getPort()+":"+msg);

                if (msg.equals("bys")){
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            socket.close();
        }

    }
}
