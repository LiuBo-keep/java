package jaxws.serive;


import javax.jws.WebService;

/**
 * @ClassName HelloWSImpl
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/8 22:57
 */
@WebService
public class HelloWSImpl implements HelloWS{
    @Override
    public String sayHello(String name) {
        System.out.println("server sayHello()"+name);
        return "Hello " +name;
    }
}
