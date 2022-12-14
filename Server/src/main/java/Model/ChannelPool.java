package Model;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.*;

public class ChannelPool {
    private Connection connection;
    private BlockingQueue<Channel> pool;
    private final static int capacity = 100;
    private final static String QUEUE_NAME = "LiftRide";

    public ChannelPool() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
        factory.setHost("172.31.3.88");
        factory.setUsername("consumer");
        factory.setPassword("password");

        try {
            this.connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            System.err.println("Something Went Wrong in Connection");
            e.printStackTrace();
        }

        this.pool = new LinkedBlockingQueue<>();

        for (int i = 0; i < this.capacity; i++) {
            try {
                Channel channel = this.connection.createChannel();
                channel.queueDeclare(this.QUEUE_NAME, false, false, false, null);
                this.pool.add(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Channel takeChannel() throws InterruptedException {
        return this.pool.take();
    }

    public void add(Channel channel) {
        this.pool.offer(channel);
    }
}
