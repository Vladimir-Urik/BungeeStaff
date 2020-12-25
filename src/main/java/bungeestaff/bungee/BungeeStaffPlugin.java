package bungeestaff.bungee;

import bungeestaff.bungee.commands.*;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.listeners.*;
import bungeestaff.bungee.rabbit.MessagingService;
import bungeestaff.bungee.system.UserCache;
import bungeestaff.bungee.system.broadcast.BroadcastManager;
import bungeestaff.bungee.system.cooldown.CooldownManager;
import bungeestaff.bungee.system.rank.RankManager;
import bungeestaff.bungee.system.staff.StaffManager;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.system.storage.IStaffStorage;
import bungeestaff.bungee.system.storage.impl.ConnectionInfo;
import bungeestaff.bungee.system.storage.impl.MySQLStorage;
import bungeestaff.bungee.system.storage.impl.ServerConnection;
import bungeestaff.bungee.system.storage.yml.YMLStorage;
import bungeestaff.bungee.util.TextUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BungeeStaffPlugin extends Plugin {

    @Getter
    private static BungeeStaffPlugin instance;

    private Config config;
    private Config messages;

    @Getter
    private StaffManager staffManager;
    @Getter
    private RankManager rankManager;
    @Getter
    private CooldownManager cooldownManager;
    @Getter
    private BroadcastManager broadcastManager;

    @Getter
    private MessagingService messagingService;

    @Getter
    private UserCache userCache;

    @Override
    public void onEnable() {
        instance = this;

        CommandSender console = getProxy().getConsole();
        TextUtil.sendMessage(console, "&8&m                        ");
        TextUtil.sendMessage(console, "&eBungeeStaff &7(&f" + getDescription().getVersion() + "&7)");
        TextUtil.sendMessage(console, "&8&m                        ");

        this.config = new Config(this, "config");
        config.load();
        this.messages = new Config(this, "messages");
        messages.load();

        this.userCache = new UserCache(this, ProxyServer.getInstance().getName());

        IStaffStorage storage = initializeStorage(null);

        this.staffManager = new StaffManager(this, storage);
        this.rankManager = new RankManager(this);

        this.cooldownManager = new CooldownManager(this);
        this.broadcastManager = new BroadcastManager(this);

        broadcastManager.load();
        cooldownManager.load();

        rankManager.load();
        staffManager.load();

        this.messagingService = new MessagingService(this);
        if (getConfig().getBoolean("Rabbit.Enabled", false)) {
            messagingService.initialize();
            userCache.setMessaging(true);
        }

        registerCommands();
        staffManager.startAutoSave();
        TextUtil.sendMessage(console, "&8&m                        ");
    }

    private IStaffStorage initializeStorage(String override) {
        String type = override == null ? getConfig().getString("storage.type", "yml").toLowerCase() : override;

        switch (type) {
            case "yml":
            case "yaml":
            case "file":
            case "flatfile":
                return new YMLStorage(this);
            case "mysql":
            case "sql":
                ConnectionInfo connectionInfo = ConnectionInfo.load(getConfig().getSection("storage.mysql"));

                if (connectionInfo == null) {
                    getLogger().warning("Could not initialize mysql database. Using yml flatfile instead.");
                    return initializeStorage("yml");
                }

                ServerConnection connection = new ServerConnection(connectionInfo);

                return new MySQLStorage(this, connection, getConfig().getString("storage.mysql.tables.users"));
            default:
                return null;
        }
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        config.load();
        messages.load();

        if (!messagingService.isInitialized() && getConfig().getBoolean("Rabbit.Enabled", false))
            messagingService.initialize();

        broadcastManager.load();
        rankManager.load();

        staffManager.reloadAutoSave();

        TextUtil.sendMessage(sender, messages.getMessage("BungeeStaff-Module.Reload")
                .replace("%time%", String.valueOf(System.currentTimeMillis() - start)));
    }

    public void onDisable() {
        staffManager.stopAutoSave();

        staffManager.save();
        messagingService.close();

        instance = null;
    }

    private void registerCommands() {

        registerCommands(new CoreCommand(this),
                new StaffChatCommand(this),
                new RequestCommand(this),
                new ReportCommand(this),
                new ToggleCommand(this),
                new StaffFollowCommand(this),
                new StaffListCommand(this),
                new BroadcastCommand(this));

        if (getConfig().getBoolean("Use-Tab-Completion")) {
            new TabCompleteListener(this).register();
        }

        registerListeners(new ChatListener(this),
                new QuitListener(this),
                new JoinListener(this),
                new ConnectionListener(this));
    }

    private void registerListeners(EventListener... listeners) {
        for (EventListener listener : listeners) {
            listener.register();
        }
    }

    private void registerCommands(Command... commands) {
        for (Command command : commands) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    public boolean hasCustomPermission(String key, CommandSender... senders) {
        for (CommandSender sender : senders) {
            String perm = getConfig().getString("Custom-Permissions." + key);
            if (!sender.hasPermission(perm) && !Strings.isNullOrEmpty(perm))
                return false;
        }
        return true;
    }

    public void sendMessage(CommandSender sender, String key) {
        String message = messages.getMessage(key);
        TextUtil.sendMessage(sender, message);
    }

    @NotNull
    public String getPrefix(ProxiedPlayer player) {
        return getPrefix(player.getUniqueId());
    }

    @NotNull
    public String getPrefix(StaffUser user) {
        return user == null || user.getRank() == null || user.getRank().getPrefix() == null ? getConfig().getString("No-Rank") : user.getRank().getPrefix();
    }

    @NotNull
    public String getPrefix(UUID uniqueID) {
        StaffUser user = staffManager.getUser(uniqueID);
        return getPrefix(user);
    }

    /**
     * Fetch StaffUser from StaffManager, if null, send the player an error message.
     */
    public StaffUser getUser(ProxiedPlayer player) {
        StaffUser user = staffManager.getUser(player);
        if (user == null)
            sendMessage(player, "General.You-Are-Not-Staff");
        return user;
    }

    public String getMessage(String key) {
        return messages.getMessage(key);
    }

    public Configuration getMessages() {
        return messages.getConfiguration();
    }

    public Configuration getConfig() {
        return config.getConfiguration();
    }
}
