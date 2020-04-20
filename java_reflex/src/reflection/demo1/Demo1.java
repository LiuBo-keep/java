package reflection.demo1;

/**
 * @ClassName Demo1
 * @Description TODO
 * @Author 17126
 * @Date 2020/4/20 12:22
 */

public class Demo1 {

    public static void main(String[] args) throws ClassNotFoundException {
        //通过反射得到类的Class对象(方式1：通过Class类中的forName方法得到)
        Class c1=Class.forName("reflection.demo1.User");
        //User类映射的Class类的对象是：class reflection.demo1.User
        System.out.println("User类映射的Class类的对象是："+c1);

        //一个类在内存中只有一个Class类的对象
        //类被加载后类的整个结构都会封装到Class的对象中
        Class c2=Class.forName("reflection.demo1.User");
        Class c3=Class.forName("reflection.demo1.User");
        //true
        System.out.println(c2.hashCode()==c3.hashCode());
    }
}


/***
 *功能描述
 *@author
 *@date
  * @param
 *@return
*/
class User{
    private String name;
    private String age;

    public User() {
    }

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}

