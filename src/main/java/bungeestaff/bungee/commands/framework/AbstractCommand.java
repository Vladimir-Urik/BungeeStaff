package bungeestaff.bungee.commands.framework;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
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
        super(name);
        this.plugin = plugin;
    }

    public AbstractCommand(BungeeStaffPlugin plugin, String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
    }

    public abstract void execute(CommandSender sender, String[] args);

    protected boolean checkPreconditions(CommandSender sender, String[] args) {

        if (!plugin.hasCustomPermission(getPermissionKey(), sender)) {
            TextUtil.sendMessage(sender, plugin.getMessages().getString("No-Permission"));
            return false;
        }

        if (getSubCommands().containsKey(args[0].toLowerCase())) {
            SubCommand subCommand = getSubCommands().get(args[0].toLowerCase());

            if (!checkRange(sender, subCommand.getRange(), args.length - 1))
                return false;

            String[] cutArgs = Arrays.copyOfRange(args, 1, args.length);

            subCommand.getExecutor().onCommand(sender, cutArgs);
            return true;
        }

        if (!checkRange(sender, getRange(), args.length))
            return false;

        if (isPlayerOnly() && !(sender instanceof ProxiedPlayer)) {
            //TODO msg
            return false;
        }

        if (isConsoleOnly() && sender instanceof ProxiedPlayer) {
            //TODO msg
            return false;
        }

        return true;
    }

    protected boolean checkRange(CommandSender sender, @Nullable Range range, int length) {
        if (range != null) {
            int res = range.check(length);

            if (res == -1) {
                TextUtil.sendMessage(sender, plugin.getMessages().getString("Not-Enough-Arguments"));
                return false;
            } else if (res == 1) {
                TextUtil.sendMessage(sender, plugin.getMessages().getString("Too-Many-Arguments"));
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
        SubCommand subCommand = new SubCommand(plugin, str);
        this.subCommands.put(str, subCommand);
        return subCommand;
    }
}
