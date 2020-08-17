package 聊天室;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * @ClassName SendThread
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 22:08
 */
public class SendThread implements Runnable{

    DatagramPacket packet = null;
    DatagramSocket socket = null;
    BufferedReader reader = null;
    private int fromPort;
    private String toIp;
    private int toPort;

    public SendThread(int fromPort, String toIp, int toPort) {
        this.fromPort = fromPort;
        this.toIp = toIp;
        this.toPort = toPort;
        try {
            socket = new DatagramSocket(fromPort);
            //读取控制台数据
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true){
                String line = reader.readLine();
                byte[] bytes = line.getBytes();
                packet =new DatagramPacket(bytes,0,bytes.length,new InetSocketAddress(this.toIp,this.toPort));

                socket.send(packet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            socket.close();
        }
    }
}
