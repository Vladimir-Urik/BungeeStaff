package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import com.google.common.base.Strings;
import com.rabbitmq.client.*;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class MessagingManager {

    public static final String EXCHANGE = "bungee-staff";

    private final BungeeStaffPlugin plugin;

    private final String serverId = UUID.randomUUID().toString();

    private Channel channel;

    private final Map<String, Set<CachedUser>> fetchedUsers = new HashMap<>();

    public MessagingManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public CachedUser getUser(String name) {
        return getUsers().stream()
                .filter(u -> u.getName().equals(name))
                .findAny().orElse(null);
    }

    public Set<CachedUser> getUsers() {
        Set<CachedUser> total = new HashSet<>();
        this.fetchedUsers.keySet().forEach(c -> total.addAll(getUsers(c)));
        return total;
    }

    public Set<CachedUser> getUsers(String serverId) {
        if (fetchedUsers.containsKey(serverId))
            return fetchedUsers.get(serverId);

        Set<CachedUser> users = new HashSet<>();
        fetchedUsers.put(serverId, users);
        return users;
    }

    public void addUser(ProxiedPlayer player) {
        getUsers(serverId).add(new CachedUser(player.getName(), player.getServer().getInfo().getName()));
    }

    public void updateUsers(String serverId, Collection<CachedUser> users) {
        this.fetchedUsers.put(serverId, new HashSet<>(users));
    }

    public void removeUser(String user) {
        this.fetchedUsers.values().forEach(list -> list.removeIf(p -> p.getName().equals(user)));
    }

    public void processUserUpdate(String serverId, String message) {

        if (Strings.isNullOrEmpty(message))
            return;

        Set<CachedUser> users = new HashSet<>();
        for (String arg : message.trim().split(";")) {
            String[] arr = arg.trim().split("=");

            String name = arr[0];
            String server = arr[1];

            users.add(new CachedUser(name, server));
        }
        updateUsers(serverId, users);
    }

    public void sendUserUpdate() {
        String message = plugin.getProxy().getPlayers().stream()
                .map(player -> player.getName() + "=" + player.getServer().getInfo().getName())
                .collect(Collectors.joining(";"));
        ProxyServer.getInstance().getLogger().info(message);
        sendMessage(MessageType.UPDATE_USERS, message);
    }

    public void initialize() {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(plugin.getConfig().getString("Rabbit.Host", "localhost"));
        factory.setPort(plugin.getConfig().getInt("Rabbit.Port", 5672));
        factory.setUsername(plugin.getConfig().getString("Rabbit.Username", "root"));
        factory.setPassword(plugin.getConfig().getString("Rabbit.Password"));

        try {
            Connection connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE, "fanout");
        } catch (TimeoutException | IOException e) {
            ProxyServer.getInstance().getLogger().severe("Could not connect to RabbitMQ.");
            e.printStackTrace();
        } finally {
            ProxyServer.getInstance().getLogger().info("Initialized a connection to RabbitMQ.");
        }

        startListening();
        startUserUpdates();
    }

    private ScheduledTask updateTask;

    public void startUserUpdates() {
        int interval = plugin.getConfig().getInt("Rabbit.User-Update-Interval", 10);

        if (updateTask != null)
            updateTask.cancel();

        this.updateTask = plugin.getProxy().getScheduler().schedule(plugin, this::sendUserUpdate, 1, interval, TimeUnit.SECONDS);
    }

    @Data
    private static class Pair<X, Y> {
        private final X key;
        private final Y value;
    }

    private Pair<MessageType, String> processId(Delivery message) {
        String messageId = message.getProperties().getMessageId();

        String[] arr = messageId.split(";");

        if (arr.length < 2 || serverId.equals(arr[1]))
            return null;

        return new Pair<>(MessageType.fromString(arr[0]), serverId);
    }

    public void startListening() {

        DeliverCallback callback = (consumerTag, message) -> {
            try {
                Pair<MessageType, String> result = processId(message);

                if (result == null)
                    return;

                MessageType type = result.getKey();
                String serverId = result.getValue();

                String content = new String(message.getBody(), StandardCharsets.UTF_8);
                type.dispatch(plugin, content, serverId);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
