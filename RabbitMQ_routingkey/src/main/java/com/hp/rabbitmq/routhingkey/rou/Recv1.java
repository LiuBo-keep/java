package com.hp.rabbitmq.routhingkey.rou;

import com.hp.rabbitmq.routhingkey.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv1 {

    private static final String EXCHANGE_NAME="test_routhing_key";
    private static final String QUEUE_NAME="test_error_queue1";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection=ConnectionUtils.getConnection();

        final Channel channel=connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        channel.basicQos(1);

        //消息队列订阅交换机，并且消息队列带一个routhingKey
        String routhingKey1="error";
        String routhingKey2="info";
        String routhingKey3="warning";
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routhingKey1);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routhingKey2);
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,routhingKey3);


        //定义消费者
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");

                System.out.println("Recv1:"+msg);

                channel.basicAck(envelope.getDeliveryTag(),false);

            }
        };

        channel.basicConsume(QUEUE_NAME,false,consumer);
    }
}
