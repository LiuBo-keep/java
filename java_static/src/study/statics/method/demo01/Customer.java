package study.statics.method.demo01;

/**
 * @ClassName Customer
 * @Description TODO
 * @Author liubo
 * @Date 2020/12/21 15:52
 */
public class Customer {
    String name;
    public Customer(String name){
        this.name = name;
    }
    public void shopping(){
        //直接访问当前对象的name
        System.out.println(name + "正在选购商品！");
        //继续让当前对象去支付
        pay();
    }
    public void pay(){
        System.out.println(name + "正在支付！");
    }
}
