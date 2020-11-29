package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import com.rabbitmq.client.*;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MessagingManager {

    private final BungeeStaffPlugin plugin;

    public static final String EXCHANGE = "bungee-staff";

    private final String serverId = UUID.randomUUID().toString();

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public MessagingManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        this.factory = new ConnectionFactory();

        factory.setHost(plugin.getConfig().getString("Rabbit.Host", "localhost"));
        factory.setPort(plugin.getConfig().getInt("Rabbit.Port", 5672));
        factory.setUsername(plugin.getConfig().getString("Rabbit.Username", "root"));
        factory.setPassword(plugin.getConfig().getString("Rabbit.Password"));

        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE, "fanout");
        } catch (TimeoutException | IOException e) {
            ProxyServer.getInstance().getLogger().severe("Could not connect to RabbitMQ.");
            e.printStackTrace();
        } finally {
            ProxyServer.getInstance().getLogger().info("Initialized a connection to RabbitMQ.");
        }

        startListening();
    }

    private MessageType processId(Delivery message) {
        String messageId = message.getProperties().getMessageId();

        String[] arr = messageId.split(";");

        if (arr.length < 2 || serverId.equals(arr[1]))
            return null;

        return MessageType.fromString(arr[0]);
    }

    public void startListening() {

        DeliverCallback callback = (consumerTag, message) -> {

            MessageType type = processId(message);

            if (type == null)
                return;

            String content = new String(message.getBody(), StandardCharsets.UTF_8);
            type.dispatch(plugin, content);
        };

        String queueName;
        try {
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE, "");

            channel.basicConsume(queueName, true, callback, consumerTag -> {
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ProxyServer.getInstance().getLogger().info("Started listening for RabbitMQ messages.");
        }
    }

    public void sendMessage(MessageType type, String message) {
        try {
            channel.basicPublish(EXCHANGE, "",
                    new AMQP.BasicProperties.Builder()
                            .messageId(type.toString() + ";" + serverId)
                            .build(),
                    message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
