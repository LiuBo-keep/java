package jaxws2.service;


/**
 * @ClassName HelloServiceImpl
 * @Description 服务接口的实现
 * @Author 17126
 * @Date 2020/6/8 23:49
 */

public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String name) {
        return name+"Welcome to you";
    }
}
