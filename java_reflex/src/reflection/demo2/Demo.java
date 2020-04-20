package reflection.demo2;

/**
 * @ClassName Demo
 * @Description 测试class类的创建方式有哪些
 * @Author 17126
 * @Date 2020/4/20 12:53
 */
public class Demo {

    public static void main(String[] args) throws ClassNotFoundException {
        Person student=new Student();
        System.out.println("角色："+student.name);

        //方式1：通过对象获得
        Class c1=student.getClass();
        System.out.println(c1.hashCode());

        //方式2：forName获取
        Class c2=Class.forName("reflection.demo2.Student");
        System.out.println(c2.hashCode());

        //方式3：通过类名.class获得
        Class c3=Student.class;
        System.out.println(c3.hashCode());

        //方式4：基本内置类型的包装类都有一个Type属性
        Class c4=Integer.TYPE;
        System.out.println(c4.hashCode());

        //获取父类类型
        Class c5=c1.getSuperclass();
        System.out.println("Student类的父类是:"+c5);
    }
}

class Person{
    public String name;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }
}

class Student extends Person{
    public Student() {
        this.name="学生";
    }
}

class Teacher extends Person{
    public Teacher() {
        this.name="学生";
    }
}
