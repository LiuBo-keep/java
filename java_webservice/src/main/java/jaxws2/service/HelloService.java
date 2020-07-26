package jaxws2.service;

import javax.jws.WebService;

/**
 * @ClassName HelloService
 * @Description 对外发布服务的接口
 * @Author 17126
 * @Date 2020/6/8 23:47
 */
@WebService
public interface HelloService {
    //对外发布服务的接口的方法
    public String sayHello(String name);
}
