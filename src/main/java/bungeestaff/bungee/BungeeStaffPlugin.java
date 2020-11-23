package bungeestaff.bungee;

import bungeestaff.bungee.commands.*;
import bungeestaff.bungee.listeners.*;
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

    private static BungeeStaffPlugin instance;

    private Config config;
    private Config messages;
    private Config settings;

    @Getter
    private StaffManager staffManager;

    //TODO move

    // Staff that has staffchat enabled
    public ArrayList<ProxiedPlayer> staffChat = new ArrayList<ProxiedPlayer>();

    // Staff that's online?
    public ArrayList<ProxiedPlayer> staffonline = new ArrayList<ProxiedPlayer>();

    // Some random cooldowns
    public ArrayList<ProxiedPlayer> requestcooldown = new ArrayList<ProxiedPlayer>();
    public ArrayList<ProxiedPlayer> reportcooldown = new ArrayList<ProxiedPlayer>();

    //TODO a singleton here? should be always loaded by class loader no?
    public static BungeeStaffPlugin getInstance() {
        if (instance == null) {
            instance = new BungeeStaffPlugin();
        }
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        CommandSender console = getProxy().getConsole();
        TextUtil.sendMessage(console, "&8&n------------------------" +
                "\n&fBungeeStaff &8- (&ev" + getDescription().getVersion() + "&8)" +
                "\n&8&n------------------------");

        this.staffManager = new StaffManager(this);

        config = Config.obtain(this, getProxy().getPluginsFolder() + "/config.yml");
        messages = Config.obtain(this, getProxy().getPluginsFolder() + "/messages.yml");
        settings = Config.obtain(this, getProxy().getPluginsFolder() + "/settings.yml");

        staffManager.load();

        registerCommands();
    }

    public void onDisable() {
        instance = null;
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
        new JoinEvent();
        new ProxyPing();
        new ConnectionListener(this).register();
    }

    private void registerCommands(Command... commands) {
        for (Command command : commands) {
            getProxy().getPluginManager().registerCommand(this, command);
        }
    }

    public Configuration getMessages() {
        return messages.getConfiguration();
    }

    public Configuration getConfig() {
        return config.getConfiguration();
    }

    public Configuration getSettings() {
        return settings.getConfiguration();
    }

    public List<ProxiedPlayer> getStaffChat() {
        return staffChat;
    }
}
