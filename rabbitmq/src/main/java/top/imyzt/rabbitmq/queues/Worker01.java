package top.imyzt.rabbitmq.queues;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by imyzt on 2018/10/24 17:08
 * 消费者1
 */
public class Worker01 {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println("Worker01 [*] Waiting for messages. To exit press CTRL+C");

        /*
        RabbitMQ不会根据消费者反馈的Ack来保证均衡的向多个消费者发送消息.所以会导致有一些消费者很忙,有一些很悠闲.
        通过设置Qos告诉RabbitMQ,不要一次将多个消息发送给一个消费者.
        这样做的好处是只有当消费者消费完成后并反馈Ack之后,才会收到另一条消息,做到消费者负载均衡
         */
        // 每次从队列中获取的数量
        channel.basicQos(1);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Worker01 [x] Received '" + message + "'");

                try {
                    doWork(message);
                } finally {
                    System.out.println("Worker01 [x] Done");
                    // 消息处理完成确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        // 消息消费完成确认
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
    }

    /**
     * 模拟工作
     * @param message
     */
    private static void doWork(String message)  {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
