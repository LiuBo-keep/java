package jaxws2.client;

//import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * @ClassName ServerClirnt
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/9 0:10
 */
public class ServerClirnt {
    public static void main(String[] args) {
        //创建cxf代理工厂
       // JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        //设置远程访问服务端地址
       // proxyFactoryBean.setAddress("http://127.0.0.1:8000/ws/hello");
        //设置接口类型(要访问的服务接口)
      //  proxyFactoryBean.setServiceClass(HelloService.class);
        //对接口生成代理对象
       // HelloService helloService = proxyFactoryBean.create(HelloService.class);
        //通过代理对象远程服务服务方法
       // String hello = helloService.sayHello("李四");
      //  System.out.println(hello);
    }
}
