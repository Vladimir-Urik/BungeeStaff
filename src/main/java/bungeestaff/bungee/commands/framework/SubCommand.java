package bungeestaff.bungee.commands.framework;

import bungeestaff.bungee.BungeeStaffPlugin;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;

public class SubCommand extends AbstractCommand {

    @Getter
    @Setter
    private CommandExecutor executor;

    public SubCommand(BungeeStaffPlugin plugin, String name) {
        super(plugin, name);
        this.executor = (sender, args) -> {
        };
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!checkPreconditions(sender, args))
            return;

        executor.onCommand(sender, args);
    }

    public SubCommand withPermissionKey(String key) {
        setPermissionKey(key);
        return this;
    }

    public SubCommand withRange(int wanted) {
        setRange(wanted);
        return this;
    }

    public SubCommand withRange(int min, int max) {
        setRange(min, max);
        return this;
    }

    @Override
    public SubCommand withSubCommand(SubCommand subCommand) {
        super.withSubCommand(subCommand);
        return this;
    }

    public SubCommand withExecutor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    public interface CommandExecutor {
        void onCommand(CommandSender sender, String[] args);
    }
}
