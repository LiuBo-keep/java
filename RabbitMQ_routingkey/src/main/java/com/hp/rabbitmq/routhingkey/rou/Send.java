package com.hp.rabbitmq.routhingkey.rou;

import com.hp.rabbitmq.routhingkey.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private static final String EXCHANGE_NAME="test_routhing_key";
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection=ConnectionUtils.getConnection();

        Channel channel=connection.createChannel();

        //声明交换机
        String type="direct";//交换机模式为路由键模式
        channel.exchangeDeclare(EXCHANGE_NAME,type);

        //发送消息
        String msg="hello routhing";
        String routhing_key="info";
        channel.basicPublish(EXCHANGE_NAME,routhing_key,null,msg.getBytes());

        channel.close();
        connection.close();
    }
}
