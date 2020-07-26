package jaxws.serive;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @ClassName HelloWS
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/8 22:55
 */


@WebService
public interface HelloWS {
    @WebMethod
    public String sayHello(String name);
}
