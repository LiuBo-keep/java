package com.hp.rabbitmq.routhingkey.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

    /**
     * 获取连接MQ的连接
     * @return
     */
    public static Connection getConnection() throws IOException, TimeoutException {
        //定义一个连接工厂
        ConnectionFactory factory=new ConnectionFactory();

        //设置服务地址
        factory.setHost("127.0.0.1");

        //设置端口号（MQ走的是AMQP协议：5672）
        factory.setPort(5672);

        //设置操作数据库（vhost）
        factory.setVirtualHost("/vhost_user");

        //设置用户名（可查看可操作此数据库的用户）
        factory.setUsername("user");

        //设置密码
        factory.setPassword("123");

        //获取连接并返回
        return factory.newConnection();
    }
}
