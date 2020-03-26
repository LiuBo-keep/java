## AOP概念
aop：面向切面编程，扩展功能不修改源代码实现
AOP采用横行抽取机制，取代了传统纵向继承体系重复代码

## AOP原理
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200326132229359.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020032613233282.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzMDcyMzk5,size_16,color_FFFFFF,t_70)

## cglib动态代理
```
注意：使用cglib动态代理时，父类不能用final修饰，以及将要被增强的方法也不能用final修饰
代码：
public class Fu {
    public void add(){
        System.out.println("添加");
    }
}
public class Zi extends Fu{
    @Override
    public void add() {
        System.out.println("开始");
        super.add();
        System.out.println("结束");
    }
}
public class Demo {
    public static void main(String[] args) {
        Zi z=new Zi();
        z.add();
    }
}
```

## AOP操作术语
```
joinpoint：连接点（类里面哪些方法可以增强，这些方法称为连接点）
Pointcat：切入点（实际增强的方法称为切入点）**
Advice：通知/增强（增强的内容）**
通知又可以分为：
前置通知（在方法之前执行）
后置通知（在方法之后执行）
异常通知（方法出现异常）
最终通知（在后置之后执行）
环绕通知（在方法前后都有执行）
Target：目标对象（代理的目标对象，即要增强的类）
Weaving：织入
Aspect:切面（把增强或通知应用到具体方法(切入点)上面，过程称为切面）**
```

## Spring的AOP操作
```
1.在spring里面进行aop操作，使用aspectj实现（aspectj不是spring的一部分，和spring一起使用）

2.使用aspectj实现aop有两种方式
2.1：基于spring的xml配置
列如：
//目标对象
class Book{
    public void add(){
        System.out.println("add......")
    }
}
//通知类
class MyBook{
    //前置通知
    public void before(){
        System.out.println("前置通知")
    }
}
/*
*1.切入点：实际增强的方法
*2.常用的表达式：
*execution(<访问修饰符>?<返回类型><方法名>(<参数>)<异常>)
*写法：
*1.execution(* com.hp.Book(目标类的全路径).add(..))//对Book类中的add方法进行增强
*2.execution(* com.hp.Book.*(..))//对Book类中的所有方法进行增强
*3.execution(* *.*(..))//所有类中的所有方法都增强
*4.execution(* *.save*(..))//匹配所有save开头的方法增强
*/
//xml配置
//1.配置对象
//要增强的目标类
<bean id="book" class="com.hp.Book"></bean>
//包含增强内容的通知类
<bean id="mybook" class="com.hp.MyBook"></bena>
//2.配置aop操作
<aop:config>
    //配置切入点
     <aop:pointcat execution(* com.hp.Book.add(..)) id="pointcat1"/>//id是给切入点起个名字任意
    //配置切面
    <aop:aspect ref="mybook">//ref是通知类
         //method代表在通知类使用哪个方法作为前置
         //pointcat-ref代表前面所指的要增强的方法的切入点名称
        <aop:before method="before" pointcat-ref="pointcat1">//前置
        <aop:after-returning method="before" pointcat-ref="pointcat1">//后置
        .....
    </aop:aspect>
</aop:config>
2.2：基于spring的注解方式
列如：
//目标对象
class Book{
    public void add(){
        System.out.println("add......")
    }
}
//通知类
@Aspect
class MyBook{
    //前置通知
    @Before(value="execution(* com.hp.Book.add(..))")//value代表给哪个目标类的哪个方法进行增强
    public void before1(){
        System.out.println("前置通知")
    }
    其他注解：
    @AfterReturning后置通知
    @Around环绕通知
    @AfterThrowing异常通知
    @After最终通知（不管是否异常，该通知都会执行）
}

xml配置
1.配置对象
//要增强的目标类
<bean id="book" class="com.hp.Book"></bean>
//包含增强内容的通知类
<bean id="mybook" class="com.hp.MyBook"></bena>
2.在spring核心配置文件中，开启aop操作
<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
3.在通知类/增强类上添加注解,并且在方法上使用注解完成增强配置
```
