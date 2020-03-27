package com.hp.rabbitmq.workqueuegongping;

import com.hp.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Recv2 {
    private static final String QUEUE_NAME="test_work_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        //得到连接
        Connection connection=ConnectionUtils.getConnection();
        //得到通道
        final Channel channel=connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //保证一次只分发一个
        channel.basicQos(1);
        //定义一个消费者
        Consumer consumer=new DefaultConsumer(channel){
            //队列内一有消息触发这个方法
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");
                System.out.println("Recv_2"+msg);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println("Recv2 done");
                    //手动反馈
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };

        //消费者监听队列（将参数2自动应答改为false）
        channel.basicConsume(QUEUE_NAME,false,consumer);

    }
}
