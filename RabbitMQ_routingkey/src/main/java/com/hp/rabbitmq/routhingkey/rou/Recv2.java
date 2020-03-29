package com.hp.rabbitmq.routhingkey.rou;

import com.hp.rabbitmq.routhingkey.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv2 {

    private static final String EXCHANGE_NAME="test_routhing_key";
    private static final String QUEUE_NAME="test_error_queue2";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection=ConnectionUtils.getConnection();

        final Channel channel=connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        channel.basicQos(1);

        //消息队列订阅交换机，并且消息队列带一个routhingKey
        String routhingKey="error";
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routhingKey);

        //定义消费者
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");

                System.out.println("Recv2:"+msg);

                channel.basicAck(envelope.getDeliveryTag(),false);

            }
        };

        channel.basicConsume(QUEUE_NAME,false,consumer);
    }
}
