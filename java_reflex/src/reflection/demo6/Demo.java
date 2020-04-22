package reflection.demo6;

import reflection.demo5.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @ClassName Demo
 * @Description 通过反射动态创建对象
 * @Author 17126
 * @Date 2020/4/23 0:10
 */
public class Demo {
    public static void main(String[] args) throws Exception {
        Class<?> aClass = Class.forName("reflection.demo5.User");
        System.out.println(aClass);

        //构造一个对象
        User user = (User) aClass.newInstance();//调用无参构造(默认使用无参构造)
        user.add();
        Constructor<?> constructor = aClass.getConstructor(String.class, String.class);//获取指定带参构造
        User lis = (User) constructor.newInstance("lis", "12");
        System.out.println(lis);

        //通过反射调用方法
        Method add = aClass.getDeclaredMethod("add", null);//获取指定方法
        add.invoke(lis);//使用invoke方法（参数1：调用该add方法的对象，参数2：add方法的参数）

        //通过反射操作属性
        User user1= (User) aClass.newInstance();
        Field name = aClass.getDeclaredField("name");//获取指定属性

        //不能直接操作私有属性，可以通过关闭程序的安全检测，属性或方法的setAccessible(true)。
        name.setAccessible(true);//设置属性权限检查默认是开启的，现在关闭就可以直接给赋值
        name.set(user1,"王五");//给属性赋值（参数1：指定哪个对象，参数2：属性值）
        System.out.println(user1.getName());
    }
}
