package demo5;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Set;
import java.util.SortedMap;

/**
 * @ClassName NioTest5
 * @Description TODO
 * @Author 17126
 * @Date 2020/7/26 23:30
 *
 * 字符集 Charset
 */
public class NioTest5 {

    @Test
    public void fun1(){
        //获取所有字符集
        SortedMap<String, Charset> sortedMap = Charset.availableCharsets();
        Set<String> keySet = sortedMap.keySet();

        for (String k:keySet
             ) {
            System.out.println(k+"-----"+sortedMap.get(k));
        }
    }

    @Test
    public void fun2() throws IOException {
        //设置编码
        Charset charset = Charset.forName("GBK");

        //获取编码器
        CharsetEncoder encoder = charset.newEncoder();

        //获取解码器
        CharsetDecoder decoder = charset.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("好好学习");
        charBuffer.flip();

        //编码
        ByteBuffer encode = encoder.encode(charBuffer);

        for (int i = 0; i < 8; i++){
            System.out.println(encode.get());
        }

        //解码
        encode.flip();
        CharBuffer decode = decoder.decode(encode);
        System.out.println(decode.toString());
    }
}
