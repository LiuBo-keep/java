package reflection.demo5;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @ClassName Demo
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/22 23:41
 */
public class Demo {
    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        //通过反射得到User的Class类的对象
        Class userClass = User.class;

        //通过Class类的对象得到类的名字
        String name = userClass.getName();
        System.out.println(name);

        //通过Class类返回源代码中给出的基础类的简单名称
        String simpleName = userClass.getSimpleName();
        System.out.println(simpleName);
        System.out.println("------------------------------------------------------");
        //通过Class类获取User类的所有属性
        Field[] fields = userClass.getFields();//只能获取共有的属性
        for (Field r:fields
             ) {
            System.out.println(r);
        }
        System.out.println("------------------------------------------------------");
        Field[] declaredFields = userClass.getDeclaredFields();//可以获取所以属性私有和共有
        for (Field df:declaredFields
             ) {
            System.out.println(df);
        }
        System.out.println("------------------------------------------------------");
        Field sex = userClass.getField("sex");//通过指定属性名称获取（共有的）
        System.out.println(sex);
        System.out.println("------------------------------------------------------");
        Field name1 = userClass.getDeclaredField("name");//通过指定属性名称获取（私有的）
        System.out.println(name1);
        System.out.println("------------------------------------------------------");
        //通过Class类获取User类中的方法
        Method[] methods = userClass.getMethods();//获取本类及父类的所有共有方法。
        for (Method m:methods
             ) {
            System.out.println("公共的："+m);
        }
        System.out.println("------------------------------------------------------");
        Method[] declaredMethods = userClass.getDeclaredMethods();//获取本类的所有方法。
        for (Method m1:declaredMethods
             ) {
            System.out.println("所有的方法："+m1);
        }
        System.out.println("------------------------------------------------------");
        //获取指定的方法
//        Method add = userClass.getMethod("add",null);//指定方法名称，参数
//        System.out.println(add);
        System.out.println("------------------------------------------------------");
        //获取无参构造器
        Constructor[] constructors = userClass.getConstructors();
        for (Constructor c:constructors
             ) {
            System.out.println(c);
        }
    }
}
