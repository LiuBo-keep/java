package urldemo;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 22:45
 */
public class Demo1 {

    public static void main(String[] args) throws MalformedURLException {
        URL url = new URL("http://localhost:8080/helloweord/index.jsp?username=lisi");

        //协议
        System.out.println(url.getProtocol());
        //主机Ip
        System.out.println(url.getHost());
        //端口
        System.out.println(url.getPort());
        //文件
        System.out.println(url.getPath());
        //全路径
        System.out.println(url.getFile());
        //参数
        System.out.println(url.getQuery());
    }
}
