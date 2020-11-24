package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class CommandBase extends Command {

    protected BungeeStaffPlugin plugin;

    @Getter
    @Setter
    private String permissionKey;

    @Getter
    @Setter
    private boolean consoleOnly = false;

    @Getter
    @Setter
    private boolean playerOnly = false;

    public CommandBase(BungeeStaffPlugin plugin, String name) {
        super(name);
        this.plugin = plugin;
    }

    public CommandBase(BungeeStaffPlugin plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!plugin.hasCustomPermission(permissionKey, sender)) {
            TextUtil.sendMessage(sender, plugin.getMessages().getString("No-Permission"));
            return;
        }

        if (playerOnly && !(sender instanceof ProxiedPlayer)) {
            //TODO msg
            return;
        }

        if (consoleOnly && sender instanceof ProxiedPlayer) {
            //TODO msg
            return;
        }

        onCommand(sender, args);
    }
}
