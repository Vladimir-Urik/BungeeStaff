package bungeestaff.bungee;

import bungeestaff.bungee.commands.*;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.listeners.*;
import bungeestaff.bungee.rabbit.MessagingService;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.broadcast.BroadcastManager;
import bungeestaff.bungee.system.cooldown.CooldownManager;
import bungeestaff.bungee.system.rank.RankManager;
import bungeestaff.bungee.system.staff.StaffManager;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.TextUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BungeeStaffPlugin extends Plugin {

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

    @Override
    public void onEnable() {
        CommandSender console = getProxy().getConsole();
        TextUtil.sendMessage(console, "&8&m                        ");
        TextUtil.sendMessage(console, "&eBungeeStaff &7(&f" + getDescription().getVersion() + "&7)");
        TextUtil.sendMessage(console, "&8&m                        ");

        this.config = new Config(this, "config");
        config.load();
        this.messages = new Config(this, "messages");
        messages.load();

        this.staffManager = new StaffManager(this);
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
        }

        registerCommands();
        TextUtil.sendMessage(console, "&8&m                        ");
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        config.load();
        messages.load();

        if (!messagingService.isInitialized() && getConfig().getBoolean("Rabbit.Enabled", false))
            messagingService.initialize();

        broadcastManager.load();
        rankManager.load();

        TextUtil.sendMessage(sender, getMessage("BungeeStaff-Module.Reload")
                .replace("%time%", String.valueOf(System.currentTimeMillis() - start)));
    }

    public void onDisable() {
        staffManager.save();
        messagingService.close();
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
        String message = getMessage(key);
        TextUtil.sendMessage(sender, message);
    }

    /**
     * Get line or list message.
     */
    public String getMessage(String key) {
        Object obj = getMessages().get(key);
        String message = null;
        if (obj instanceof String)
            message = (String) obj;
        else if (obj instanceof List<?>)
            message = String.join("\n&r", getMessages().getStringList(key));
        return TextUtil.color(message);
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

    public Set<CachedUser> getUsers() {
        Set<CachedUser> users = messagingService.getUserCache().getUsers();
        getProxy().getPlayers().forEach(p -> users.add(new CachedUser(p)));
        return users;
    }

    public Configuration getMessages() {
        return messages.getConfiguration();
    }

    public Configuration getConfig() {
        return config.getConfiguration();
    }
}
