package study.statics.variable.demo01;

/**
 * @ClassName ManTest
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 10:49
 */
public class ManTest {
    public static void main(String[] args) {
        Man jack = new Man(100);
        System.out.println(jack.idCard + "," + (jack.sex ? "男" : "女"));
        Man sun = new Man(101);
        System.out.println(sun.idCard + "," + (sun.sex ? "男" : "女"));
        Man cok = new Man(102);
        System.out.println(cok.idCard + "," + (cok.sex ? "男" : "女"));
    }

}
