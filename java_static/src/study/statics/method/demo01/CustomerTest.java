package study.statics.method.demo01;

/**
 * @ClassName CustomerTest
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 15:52
 */
public class CustomerTest {
    public static void main(String[] args) {
        Customer jack = new Customer("jack");
        jack.shopping();
        Customer rose = new Customer("rose");
        rose.shopping();
    }
}