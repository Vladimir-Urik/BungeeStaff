package bungeestaff.bungee;

import bungeestaff.bungee.commands.*;
import bungeestaff.bungee.listeners.*;
import bungeestaff.bungee.system.cooldown.CooldownManager;
import bungeestaff.bungee.system.rank.RankManager;
import bungeestaff.bungee.system.staff.StaffManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    //TODO move

    // Staff that has staffchat enabled
    public ArrayList<ProxiedPlayer> staffChat = new ArrayList<>();

    // Staff that's online?
    public ArrayList<ProxiedPlayer> staffonline = new ArrayList<>();

    // Some random cooldowns
    public ArrayList<ProxiedPlayer> requestcooldown = new ArrayList<>();
    public ArrayList<ProxiedPlayer> reportcooldown = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;

        CommandSender console = getProxy().getConsole();
        TextUtil.sendMessage(console, "&8&n------------------------" +
                "\n&fBungeeStaff &8- (&ev" + getDescription().getVersion() + "&8)" +
                "\n&8&n------------------------");

        this.config = new Config(this, "config");
        config.load();
        this.messages = new Config(this, "messages");
        messages.load();

        this.staffManager = new StaffManager(this);
        this.rankManager = new RankManager(this);

        this.cooldownManager = new CooldownManager(this);

        cooldownManager.load();

        rankManager.load();
        staffManager.load();

        registerCommands();
    }

    public void reload(CommandSender sender) {
        long start = System.currentTimeMillis();

        config.load();
        messages.load();

        rankManager.load();
        staffManager.load();

        TextUtil.sendMessage(sender, getMessages().getString("BungeeStaff-Module.Reload")
                .replace("%time%", String.valueOf(System.currentTimeMillis() - start)));
    }

    public void onDisable() {
        instance = null;
        staffManager.save();
    }

    private void registerCommands() {

        registerCommands(new CoreCommand(this),
                new StaffChatCMD(),
                new RequestCMD(),
                new ReportCommand(this),
                new ToggleSM(),
                new StaffFollow(),
                new StaffList());

        if (getConfig().getBoolean("Broadcast.Use-Broadcast"))
            registerCommands(new BroadcastCommand(this));

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
            if (!sender.hasPermission(getConfig().getString("Custom-Permissions." + key)))
                return false;
        }
        return true;
    }

    public void sendMessage(String message, Collection<ProxiedPlayer> players) {
        players.forEach(p -> TextUtil.sendMessage(p, message));
    }

    public void sendMessage(String message, CommandSender... senders) {
        Arrays.stream(senders).forEach(sender -> TextUtil.sendMessage(sender, message));
    }

    public String getLineMessage(String key) {
        return TextUtil.color(getMessages().getString(key));
    }

    public void sendLineMessage(String key, CommandSender... senders) {
        String message = getLineMessage(key);
        sendMessage(message, senders);
    }

    public String getListMessage(String key) {
        return String.join("\n", getMessages().getStringList(key));
    }

    public void sendListMessage(String key, CommandSender... senders) {
        String message = getListMessage(key);
        sendMessage(message, senders);
    }

    public Configuration getMessages() {
        return messages.getConfiguration();
    }

    public Configuration getConfig() {
        return config.getConfiguration();
    }

    public List<ProxiedPlayer> getStaffChat() {
        return staffChat;
    }
}
