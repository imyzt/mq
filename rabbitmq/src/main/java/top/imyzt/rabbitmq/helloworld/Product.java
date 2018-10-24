package top.imyzt.rabbitmq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/24 15:35
 * 消息生产者
 */
public class Product {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {

        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 创建一个新的连接
        Connection connection = factory.newConnection();
        // 创建一个频道
        Channel channel = connection.createChannel();
        /**
         *  声明一个队列
         *  在RabbitMQ中声明队列是幂等性的(幂等性操作的特点是多次执行所产生的影响与第一次执行的影响相同)
         *  也就是说,如果不存在,就创建. 如果存在,不会对已存在的产生任何影响
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "hello world";

        // 发送消息到消息队列
        for (int i = 0; i < 100; i++) {
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println("Product [x] Sent '" + message + "'");
        }

        // 关闭频道和连接
        channel.close();
        connection.close();
    }

}
