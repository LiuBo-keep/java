package com.hp.rabbitmq.workqueuelunxun;

import com.hp.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Recv1 {
    private static final String QUEUE_NAME="test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //得到连接
        Connection connection=ConnectionUtils.getConnection();
        //得到通道
        Channel channel=connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //定义一个消费者
        Consumer consumer=new DefaultConsumer(channel){
            //队列内一有消息触发这个方法
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");
                System.out.println("Recv_1"+msg);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv1 done");
                }
            }
        };

        //消费者监听队列
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }
}
