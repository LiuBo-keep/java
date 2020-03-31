package com.hp.java.net.demo1;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 如果一个类没有构造方法：
 * A：成员变量都是静态的
 * B：单例模式
 * C：类中有静态方法返回该类对象（InetAddress）
 *      class Demo{
 *          private Demo(){
 *
 *          }
 *          public static Demo getXX(){
 *              return new Demo();
 *          }
 *      }
 *
 *  InetAddress类中的成员方法：
 *  public static InetAddress getByName(String host);//根据主机名或者IP地址的字符串表示得到IP地址对象
 *  public String getHostName();//得到主机名
 *  public String getHostAddress();//得到IP地址
 */
public class InetAddressDemo {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress inetAddress=InetAddress.getByName("DESKTOP-JI4CVIU");

        String hostName=inetAddress.getHostName();
        String hostIP=inetAddress.getHostAddress();

        System.out.println(hostName+"---"+hostIP);
    }
}
