package Bio;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName ServerDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 22:03
 */
public class ServerDemo {
    public static void main(String[] args) throws  Exception{
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("服务器启动成功");
        System.out.println("等待连接");
        Socket socket = serverSocket.accept();
        System.out.println("已连接");
        InputStream inputStream = socket.getInputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer))!=-1){
            System.out.println(new String(buffer, 0, len));
        }
    }
}
