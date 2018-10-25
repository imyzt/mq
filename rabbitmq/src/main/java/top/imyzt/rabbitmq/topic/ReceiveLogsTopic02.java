package top.imyzt.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/25 11:45
 * 匹配模式的消费者02
 * 消费者定义消费规则,提供给交换器,交换器会将符合匹配规则的消息发送到消费者.<br/>
 *
 * [*] 匹配一个字符<br/>
 * [#] 匹配零个或多个字符<br/>
 * routingKey最多不能超过255个字节<br/>
 *
 * 交换器在匹配器模式下: <br/>
 * 如果消费者指定的路由关键字只使用[#]来匹配消息,在匹配[topic]模式下,他会变成分发[fanout]模式,接收所有消息.<br/>
 * 如果消费者指定的路由关键字没有[*]或[#],在匹配[topic]模式下,他会变成直连[direct]模式,接收匹配消息.
 */
public class ReceiveLogsTopic02 {

    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String[] routingKeys = new String[]{"*.*.rabbit", "lazy.#"};

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 获取随机队列名称
        String queueName = channel.queueDeclare().getQueue();

        // 声明交换器类型
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        for (String routingKey : routingKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
            System.out.println("ReceiveLogsTopic02 exchange:"+EXCHANGE_NAME+", queue:"+queueName+", BindRoutingKey:" + routingKey);
        }

        System.out.println("ReceiveLogsTopic02 [*] Waiting for messages. To exit press CTRL+C");

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("ReceiveLogsTopic02 [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };

        channel.basicConsume(queueName, true, consumer);

    }
}
