package study.statics.codeBlock.demo01;

/**
 * @ClassName StaticTest01
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 15:42
 */
public class StaticTest01 {
    //静态代码块
    static {
        System.out.println(2);
    }

    //静态代码块
    static {
        System.out.println(1);
    }

    //main方法
    public static void main(String[] args) {
        System.out.println("main execute!");
    }

    //静态代码块
    static {
        System.out.println(0);
    }
}
