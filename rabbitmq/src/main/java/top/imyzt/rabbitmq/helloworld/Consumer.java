package top.imyzt.rabbitmq.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/24 15:35
 * 消息消费者
 */
public class Consumer {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建一个新的连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();

        // 声明要关注的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Consumer [*] Waiting for messages. To exit press CTRL+C");

        /*
         * DefaultConsumer实现了Consumer接口,通过传入一个频道,告诉服务器我们需要那个频道的消息
         */
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Consumer [x] Received '" + message + "'");
            }
        };

        /*
          自动回复消息应答, RabbitMQ的消息确认机制
          true: 如果消费者收到消息后没有回复Rabbit服务器,则不删除队列中的当条消息.发送给其它消费者
         */
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
