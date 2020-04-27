package reflection.demo7;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName MyAonn
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/23 9:59
 */


//设置保留策略
@Retention(RetentionPolicy.RUNTIME)
//设置作用域
@Target(ElementType.TYPE)
@interface MyAonn {
    String name();
}
