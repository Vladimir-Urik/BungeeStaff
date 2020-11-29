package bungeestaff.bungee.commands.framework;

import bungeestaff.bungee.BungeeStaffPlugin;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCommand extends Command {

    protected final BungeeStaffPlugin plugin;

    @Getter
    @Setter
    private String permissionKey;

    @Getter
    @Setter
    private boolean consoleOnly = false;
    @Getter
    @Setter
    private boolean playerOnly = false;

    @Getter
    private Range range;

    @Getter
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public AbstractCommand(BungeeStaffPlugin plugin, String name) {
        super(name.toLowerCase());
        this.plugin = plugin;
    }

    public AbstractCommand(BungeeStaffPlugin plugin, String name, String permission, String... aliases) {
        super(name.toLowerCase(), permission, aliases);
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender sender, String[] args);

    protected boolean checkPreconditions(CommandSender sender, String[] args) {

        if (!plugin.hasCustomPermission(getPermissionKey(), sender)) {
            plugin.sendLineMessage("General.No-Permission", sender);
            return false;
        }

        if (args.length > 0)
            if (getSubCommands().containsKey(args[0].toLowerCase())) {
                SubCommand subCommand = getSubCommands().get(args[0].toLowerCase());

                String[] cutArgs = Arrays.copyOfRange(args, 1, args.length);

                ProxyServer.getInstance().getLogger().info(Arrays.toString(cutArgs));

                subCommand.execute(sender, cutArgs);
                return false;
            }

        if (!checkRange(sender, getRange(), args.length))
            return false;

        if (isPlayerOnly() && !(sender instanceof ProxiedPlayer)) {
            plugin.sendLineMessage("General.Only-Player", sender);
            return false;
        }

        if (isConsoleOnly() && sender instanceof ProxiedPlayer) {
            plugin.sendLineMessage("General.Only-Console", sender);
            return false;
        }

        return true;
    }

    protected boolean checkRange(CommandSender sender, @Nullable Range range, int length) {
        ProxyServer.getInstance().getLogger().info("" + (range == null));
        if (range != null) {
            int res = range.check(length);

            ProxyServer.getInstance().getLogger().info(range.toString() + " = " + res);

            if (res == -1) {
                plugin.sendLineMessage("General.Not-Enough-Arguments", sender);
                return false;
            } else if (res == 1) {
                plugin.sendLineMessage("General.Too-Many-Arguments", sender);
                return false;
            }
        }
        return true;
    }

    public void setRange(int min, int max) {
        this.range = new Range(min, max);
    }

    public void setRange(int wanted) {
        this.range = new Range(wanted);
    }

    public AbstractCommand withSubCommand(SubCommand subCommand) {
        this.subCommands.put(subCommand.getName(), subCommand);
        return this;
    }

    public SubCommand withSubCommand(String str) {
        SubCommand subCommand = new SubCommand(plugin, str.toLowerCase());
        this.subCommands.put(subCommand.getName(), subCommand);
        return subCommand;
    }
}
