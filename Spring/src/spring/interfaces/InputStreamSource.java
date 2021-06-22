package spring.interfaces;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Description TODO
 * @Author 17126
 * @Date 2021/6/22 14:36
 */
public interface InputStreamSource {

    /**
     * 定位并打开资源，返回资源对应的输入流。每次调用都会返回新的输入流，调用者在使用完毕后必须关闭该资源。
     */
    InputStream getInputStream() throws IOException;
}
