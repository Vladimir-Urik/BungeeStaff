package bungeestaff.bungee.commands.framework;

import bungeestaff.bungee.BungeeStaffPlugin;
import net.md_5.bungee.api.CommandSender;

public abstract class CommandBase extends AbstractCommand {

    public CommandBase(BungeeStaffPlugin plugin, String name) {
        super(plugin, name);
    }

    public CommandBase(BungeeStaffPlugin plugin, String name, String permissionKey, String... aliases) {
        super(plugin, name, "", aliases);
        setPermissionKey(permissionKey);
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!checkPreconditions(sender, args))
            return;

        onCommand(sender, args);
    }

    public CommandBase withRange(int wanted) {
        setRange(wanted);
        return this;
    }

    public CommandBase withRange(int min, int max) {
        setRange(min, max);
        return this;
    }

    public CommandBase withPermissionKey(String key) {
        setPermissionKey(key);
        return this;
    }
}
