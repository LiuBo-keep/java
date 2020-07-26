package jaxws.serive;

import javax.xml.ws.Endpoint;

/**
 * @ClassName ServerTest
 * @Description TODO
 * @Author 17126
 * @Date 2020/6/8 23:00
 */
/*
 * 发布Web Service
 */

public class ServerTest {
    public static void main(String[] args) {
        String address = "http://127.0.0.1:8989/ws/hellows";
        Endpoint.publish(address , new HelloWSImpl());
        System.out.println("发布webservice成功!");

    }
}
