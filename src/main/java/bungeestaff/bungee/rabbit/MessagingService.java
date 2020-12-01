package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.rabbit.cache.UserCache;
import bungeestaff.bungee.system.Pair;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.ParseUtil;
import com.rabbitmq.client.*;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class MessagingService {

    public static final String EXCHANGE = "bungee-staff";

    private final BungeeStaffPlugin plugin;

    // Assign random server ID to be able to identify ourselves.
    private final String serverId = UUID.randomUUID().toString();

    @Getter
    private final UserCache userCache;

    private Connection connection;
    private Channel channel;

    private ScheduledTask updateTask;

    @Getter
    private boolean initialized = false;

    public MessagingService(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
        this.userCache = new UserCache(plugin, serverId);
    }

    public void initialize() {
        ConnectionFactory factory = new ConnectionFactory();

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
            return;
        }

        ProxyServer.getInstance().getLogger().info("Initialized RabbitMQ for synchronization.");

        startListening();
        startUserUpdates();

        sendStaffUpdate();

        this.initialized = true;
    }

    public void startUserUpdates() {
        int interval = plugin.getConfig().getInt("Rabbit.User-Update-Interval", 10);

        if (updateTask != null)
            updateTask.cancel();

        this.updateTask = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            sendUserUpdate();
            sendStaffUpdate();
        }, 1, interval, TimeUnit.SECONDS);
    }

    public void sendUserUpdate() {

        if (!isInitialized())
            return;

        String message = plugin.getProxy().getPlayers().stream()
                .map(CachedUser::serializeFrom)
                .collect(Collectors.joining(","));
        sendMessage(MessageType.UPDATE_USERS, message);
    }

    public void sendStaffAdd(StaffUser user) {
        sendMessage(MessageType.STAFF_ADD, user.serialize());
    }

    public void sendStaffRemove(String name) {
        sendMessage(MessageType.STAFF_REMOVE, name);
    }

    public void sendStaffJoin(StaffUser user) {
        String str = user.getName();
        sendMessage(MessageType.STAFF_JOIN, str);
    }

    public void sendStaffQuit(StaffUser user) {
        String str = user.getName();
        sendMessage(MessageType.STAFF_LEAVE, str);
    }

    public void sendStaffChatUpdate(StaffUser user, boolean state) {
        String str = user.getName() + ";" + state;
        sendMessage(MessageType.STAFF_SC, str);
    }

    public void sendStaffMessagesToggle(StaffUser user, boolean state) {
        String str = user.getName() + ";" + state;
        sendMessage(MessageType.STAFF_TSM, str);
    }

    // Initial staff list update
    public void sendStaffUpdate() {

        if (!isInitialized())
            return;

        Set<StaffUser> local = plugin.getStaffManager().getUsers(u -> !u.isRemote());
        String str = ParseUtil.serializeCollection(local);
        sendMessage(MessageType.UPDATE_STAFF, str);
    }

    private Pair<MessageType, String> processId(Delivery message) {
        String messageId = message.getProperties().getMessageId();

        String[] arr = messageId.split(";");

        // Only accept messages that are not send from us.
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

        if (!isInitialized())
            return;

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

    public void close() {

        if (!isInitialized())
            return;

        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
