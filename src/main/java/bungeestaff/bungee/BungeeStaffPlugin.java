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

    public void onDisable() {
        instance = null;
        staffManager.save();
    }

    private void registerCommands() {

        registerCommands(new CoreCMD(),
                new StaffChatCMD(),
                new RequestCMD(),
                new ReportCMD(),
                new ToggleSM(),
                new StaffFollow(),
                new StaffList());

        if (getConfig().getBoolean("Maintenance.Use-Maintenance"))
            registerCommands(new MaintenanceCMD());

        if (getConfig().getBoolean("Broadcast.Use-Broadcast"))
            registerCommands(new BroadcastCMD());

        if (getConfig().getBoolean("Use-Tab-Completion")) {
            new TabComplete();
        }

        new ChatListener(this).register();
        new QuitEvent();
        new JoinListener(this).register();
        new PingListener();
        new ConnectionListener(this).register();
    }

    private void registerCommands(Command... commands) {
        for (Command command : commands) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    public boolean hasCustomPermission(String key, ProxiedPlayer... players) {
        for (ProxiedPlayer player : players) {
            if (!player.hasPermission(getConfig().getString("Custom-Permissions." + key)))
                return false;
        }
        return true;
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
