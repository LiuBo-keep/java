package urldemo;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @ClassName DountDemo
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/15 23:00
 */
public class DountDemo {
    public static void main(String[] args) throws Exception {
        //下载地址
        URL url = new URL("http://audio04.dmhmusic.com/71_53_T10038875176_128_4_1_0_sdk-cpm/cn/0207/M00/8D/71/ChR46118h-mAKLNLAEMoEIlqvhw964.mp3?xcode=f12c50b51956e0c46d02e161b8d8ca2a6b15ba2");

        //连接到这个资源 Http(并强转为http)
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //获取资源
        InputStream inputStream = urlConnection.getInputStream();

        //存到本地
        FileOutputStream outputStream = new FileOutputStream("w964.mp3");

        byte[] bytes = new byte[1024];
        int len;
        while ((len = inputStream.read(bytes))!=-1){
            outputStream.write(bytes,0,len);
        }

        //释放资源
        outputStream.close();
        inputStream.close();
        //断开连接
        urlConnection.disconnect();
    }
}
