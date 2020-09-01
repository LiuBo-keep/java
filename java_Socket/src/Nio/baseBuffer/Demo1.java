package Nio.baseBuffer;

import java.nio.IntBuffer;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/8/17 23:42
 */
public class Demo1 {

    public static void main(String[] args) {
        //创建一个Buffer 大小为5  即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向buffer中存放数据(intBuffer.capacity()返回缓冲区的大小)
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //读取buffer中的数据
        //将buffer转换，读写切换
        intBuffer.flip();

        //循环读出数据
        while (intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
