package com.hp.rabbitmq.subscribe.sub;

import com.hp.rabbitmq.subscribe.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private static final String EXCHANGE_NAME="text_exchange_fanout";
    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection=ConnectionUtils.getConnection();

        Channel channel=connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");//fanout 分发

        //发送信息(发给交换机)
        String msg="hello_exchange";

        channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes());

        System.out.println("Send:"+msg);

        channel.close();
        connection.close();
    }
}
