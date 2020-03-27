package com.hp.rabbitmq.workqueuelunxun;

import com.hp.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class send {
    private static final String QUEUE_NAME="test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //得到连接
        Connection connection=ConnectionUtils.getConnection();
        //得到通道
        Channel channel=connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //向队列中发送50条消息
        for (int i=1;i<=50;i++){
            String msg="hello workqueues"+i;
            channel.basicPublish("",QUEUE_NAME,null,msg.getBytes());
            TimeUnit.SECONDS.sleep(2);
            System.out.println("发送第："+i);
        }

        channel.close();
        connection.close();
    }
}
