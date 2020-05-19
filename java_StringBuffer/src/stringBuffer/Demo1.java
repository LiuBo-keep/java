package stringBuffer;

import org.junit.jupiter.api.Test;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/5/19 0:32
 */
public class Demo1 {

    @Test
    public void fun1(){
        StringBuffer stringBuffer = new StringBuffer("hello");
    }

    @Test
    public void fun2(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("hello");
    }

    @Test
    public void fun3(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("hello").append("world!");
        System.out.println(stringBuffer);
    }

    @Test
    public void fun4(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("hello").append("world!").insert(11,"good");
        System.out.println(stringBuffer);
    }

    @Test
    public void fun5(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("hello").append("world!").delete(10,11).insert(10,"good");
        System.out.println(stringBuffer);
        System.out.println(stringBuffer.capacity());
    }
}
