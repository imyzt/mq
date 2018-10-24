package top.imyzt.rabbitmq.queues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/24 17:03
 * 生产者<br/>
 * 对应多个消费者.
 */
public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 在声明队列时,声明队列需要持久化.防止RabbitMQ服务器挂掉时,丢失数据和任务
        boolean durable = true;

        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

        for (int i = 0; i < 10; i++) {
            String message = "Hello World " + i;
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println("[x] Sent '" + message + "'");
        }

        channel.close();
        connection.close();
    }

}
