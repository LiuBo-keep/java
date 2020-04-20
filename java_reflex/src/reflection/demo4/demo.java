package reflection.demo4;

import java.lang.annotation.ElementType;

/**
 * @ClassName demo
 * @Description 所有类型的class
 * @Author 17126
 * @Date 2020/4/20 13:27
 */
public class demo {

    public static void main(String[] args) {
        //类
        Class c1 = Object.class;
        //接口
        Class c2 = Comparable.class;
        //一维数组
        Class c3 = String[].class;
        //二维数组
        Class c4 = int[][].class;
        //注解
        Class c5 = Override.class;
        //枚举
        Class c6 = ElementType.class;
        //基本类型
        Class c7 = Integer.class;
        //void
        Class c8 = void.class;
    }
}
