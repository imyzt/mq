package top.imyzt.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/25 11:40
 * 匹配模式.只有匹配了routingkey的规则,交换器才会将消息发送给消费者
 */
public class TopicSend {

    private static final String EXCHANGE_NAME = "topic_logs";
    private static final String[] routingKeys = new String[]{
            "quick.orange.rabbit",
            "lazy.orange.elephant",
            "quick.orange.fox",
            "lazy.brown.fox",
            "quick.brown.fox",
            "quick.orange.male.rabbit",
            "lazy.orange.male.rabbit"
    };

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明交换器类型
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        for (String routingKey : routingKeys) {
            String message = "From " + routingKey + " routingKey' s message!";
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
            System.out.println("TopicSend [x] Sent '" + routingKey + "':'" + message + "'");
        }

        channel.close();
        connection.close();
    }

}
