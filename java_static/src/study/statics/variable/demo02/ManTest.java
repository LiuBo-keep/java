package study.statics.variable.demo02;

/**
 * @ClassName ManTest
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 12:01
 */
public class ManTest {
    public static void main(String[] args) {
        Man jack = new Man(100);
        System.out.println(jack.idCard + "," + (Man.sex ? "男" : "女"));
        Man sun = new Man(101);
        System.out.println(sun.idCard + "," + (Man.sex ? "男" : "女"));
        Man cok = new Man(102);
        System.out.println(cok.idCard + "," + (Man.sex ? "男" : "女"));
    }
}
