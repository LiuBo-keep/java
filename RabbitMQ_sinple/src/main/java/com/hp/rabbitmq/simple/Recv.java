package com.hp.rabbitmq.simple;

import com.hp.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class Recv {

    private static final String QUEUE_NAME="text_simple_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //oldAPI();老的API不建议使用
        newAPI();//建议使用
    }

    private static void newAPI() throws IOException, TimeoutException {
        //获取连接
        Connection connection=ConnectionUtils.getConnection();

        //通过连接获取一个通道(通过通道获取一个对象操作vhost数据库)
        Channel channel=connection.createChannel();

        //创建队列（例如在mysql数据库中创建表一样）
        /*
         *参数解释：
         * 参数1：queue 消息队列名称
         * 参数2：durable 是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，
         * 如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq
         * 重启之后会读取该数据
         * 参数3：exclusive 是否排外的，有两个作用，一：当连接关闭时connection.close()该队列
         * 是否会自动删除；二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都
         * 访问同一个队列，没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能
         * 访问的，如果强制访问会报异常：com.rabbitmq.client.ShutdownSignalException: channel
         * error; protocol method: #method<channel.close>(reply-code=405, reply-text=
         * RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'queue_name'
         * in vhost '/', class-id=50, method-id=20)一般等于true的话用于一个队列只能有一个消费者来消费的场景
         * 参数4：autoDelete 是否自动删除，当最后一个消费者断开连接之后队列是否自动被删除，
         * 可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * 参数5：arguments 队列中的消息什么时候会自动被删除？
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //定义消费者：事件模式，队列中一旦有消息就会触发handleDelivery方法
        DefaultConsumer consumer=new DefaultConsumer(channel){
            //获取到达的消息
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");
                System.out.println(msg);
            }
        };

        //监听队列
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

    private static void oldAPI() throws IOException, TimeoutException, InterruptedException {
        //获取连接
        Connection connection=ConnectionUtils.getConnection();

        //通过连接获取一个通道(通过通道获取一个对象操作vhost数据库)
        Channel channel=connection.createChannel();

        //定义队列的消费者
        QueueingConsumer consumer=new QueueingConsumer(channel);

        //监听队列
        /*
         *参数解释：
         * 参数1：队列名
         * 参数2：是否自动确认消息,true自动确认,false 不自动要手动调用,建立设置为false
         * 参数3：消费者
         */
        channel.basicConsume(QUEUE_NAME,true,consumer);
        while (true){
            QueueingConsumer.Delivery delivery=consumer.nextDelivery();
            String msg=new String(delivery.getBody());
            System.out.println(msg);
        }
    }
}
