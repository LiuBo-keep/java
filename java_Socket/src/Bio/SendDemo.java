package Bio;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @ClassName SendDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/27 22:03
 */
public class SendDemo {
    public static void main(String[] args) throws Exception{
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(6666));

        String str = "ABDSADA";

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(str.getBytes());
        outputStream.close();
        socket.close();
    }
}
