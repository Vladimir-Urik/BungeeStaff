package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

public class StaffChatCommand extends CommandBase {

    public StaffChatCommand(BungeeStaffPlugin plugin) {
        super(plugin, "staffchat", "StaffChat-Command", "sc");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        ProxiedPlayer player = null;
        StaffUser user = null;

        if (sender instanceof ProxiedPlayer) {
            player = (ProxiedPlayer) sender;
            user = plugin.getStaffManager().getUser(player.getUniqueId());

            if (user == null)
                return;

            if (args.length == 0) {
                plugin.sendLineMessage("StaffChat-Module.StaffChat-" + (user.switchStaffChat() ? "Enabled" : "Disabled"), player);
                return;
            }
        }

        StringBuilder message = new StringBuilder();
        Arrays.stream(args).forEach(message::append);

        // Replace placeholders here to override them in StaffManager#sendStaffMessage
        String wholeMessage = plugin.getLineMessage("StaffChat-Module.StaffChat-Message")
                .replace("%server%", player == null ? "Void" : player.getServer().getInfo().getName())
                .replace("%player%", player == null ? "Console" : player.getName())
                .replace("%prefix%", player == null ? "&6Mighty &e" : plugin.getPrefix(player))
                .replace("%rank%", player == null ? "System" : user.getRank().getName())
                .replace("%message%", message.toString());

        plugin.getStaffManager().sendRawStaffMessage(wholeMessage);
    }
}
