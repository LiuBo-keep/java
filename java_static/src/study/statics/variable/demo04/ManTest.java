package study.statics.variable.demo04;

/**
 * @ClassName ManTest
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 15:29
 */
public class ManTest {
    public static void main(String[] args) {
        //静态变量比较正式的访问方式
        System.out.println("性别 = " + Man.sex);
        //创建对象
        Man jack = new Man(100);
        //使用“引用”来访问静态变量可以吗？
        System.out.println("性别 = " + jack.sex);
        //对象被垃圾回收器回收了
        jack = null;
        //使用“引用”还可以访问吗？
        System.out.println("性别 = " + jack.sex);
        //静态方法比较正式的访问方式
        Man.printInfo();
        //访问静态方法可以使用引用吗？并且空的引用可以吗？
        jack.printInfo();
    }
}
