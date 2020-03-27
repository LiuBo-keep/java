package com.hp.rabbitmq.subscribe.sub;

import com.hp.rabbitmq.subscribe.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv1 {

    private static final String QUEUE_NAME="text_queue1";
    private static final String EXCHANGE_NAME="text_exchange_fanout";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection=ConnectionUtils.getConnection();

        final Channel channel=connection.createChannel();

        //声明队列
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //让消息队列绑定交换机（订阅）
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");

        channel.basicQos(1);

        //定义一个消费者
        Consumer consumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");
                System.out.println("Recv1"+msg);
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };

        channel.basicConsume(QUEUE_NAME,false,consumer);
    }
}
