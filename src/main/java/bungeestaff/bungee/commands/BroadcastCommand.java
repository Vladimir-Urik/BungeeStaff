package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BroadcastCommand extends CommandBase {

    public BroadcastCommand(BungeeStaffPlugin plugin) {
        super(plugin, "broadcast", "Broadcast-Command", "announce");
        setPlayerOnly(true);
        setRange(1, -1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;

        String message = String.join(" ", args);

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        String format = plugin.getListMessage("Broadcast-Module.Message")
                .replace("%player%", sender.getName())
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%message%", message)
                .replace("%prefix%", plugin.getPrefix(player));

        plugin.sendMessage(format, plugin.getProxy().getPlayers());
    }
}
