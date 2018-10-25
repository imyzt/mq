package top.imyzt.rabbitmq.touting;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/25 10:03
 * [路由] direct 消费者02
 */
public class ReceiveLogsDirect02 {

    private static final String EXCHANGE_NAME = "direct_logs";

    private static final String[] routingKeys = new String[]{"error"};

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        String queueName = channel.queueDeclare().getQueue();
        for (String routingKey : routingKeys) {
            // routingKey 声明消费者关注的消息关键字
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
            System.out.println("ReceiveLogsDirect1 exchange:" + EXCHANGE_NAME +
                    ", queue:" + queueName + ", BindRoutingKey:" + routingKey);
        }

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Consumer02 [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };

        // 消息反馈机制
        channel.basicConsume(queueName, true, consumer);
    }
}
