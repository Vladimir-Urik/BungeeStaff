package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.broadcast.BroadcastFormat;
import bungeestaff.bungee.system.broadcast.PlaceholderContainer;
import net.md_5.bungee.api.CommandSender;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BroadcastCommand extends CommandBase {

    public BroadcastCommand(BungeeStaffPlugin plugin) {
        super(plugin, "broadcast", "Broadcast-Command", "announce");
        withRange(1, -1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        BroadcastFormat format = plugin.getBroadcastManager().getFormat(args[0]);

        if (format == null) {
            plugin.sendMessage(sender, "Broadcast-Module.Invalid-Format");
            return;
        }

        StringBuilder input = new StringBuilder();
        Arrays.stream(args).skip(1).forEach(str -> input.append(" ").append(str));

        Pattern pattern = Pattern.compile("(\\S.*?)='(.*?)'");
        Matcher matcher = pattern.matcher(input.toString());

        PlaceholderContainer placeholders = new PlaceholderContainer();

        while (matcher.find())
            placeholders.add(matcher.group(1), matcher.group(2));

        plugin.getLogger().info(placeholders.toString());

        plugin.getBroadcastManager().broadcast(format, placeholders);
        plugin.sendMessage(sender, "Broadcast-Module.Broadcast-Sent");
    }
}
