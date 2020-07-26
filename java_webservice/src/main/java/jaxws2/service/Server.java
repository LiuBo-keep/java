package jaxws2.service;

//import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

/**
 * @ClassName Server
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/8 23:50
 */
public class Server {
    public static void main(String[] args) {
        //发布服务的工厂
        //JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();

        //设置服务地址
        //factoryBean.setAddress("http://127.0.0.1:8000/ws/hello");

        //设置服务类
        //factoryBean.setServiceBean(new HelloServiceImpl());

        //发布服务
        //factoryBean.create();

        System.out.println("发布服务成功");
    }
}
