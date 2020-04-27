package reflection.demo7;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @ClassName Demo
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/23 10:03
 */
public class Demo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        User user = new User();
        Class aClass = user.getClass();
        MyAonn  myAonn= (MyAonn) aClass.getAnnotation(MyAonn.class);
        System.out.println(myAonn.name() );
    }
}
