package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.rabbit.MessageType;
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

            if ((user = plugin.getUser(player)) == null)
                return;

            if (args.length == 0) {
                plugin.sendMessage(player, "StaffChat-Module.StaffChat-" + (user.switchStaffChat() ? "Enabled" : "Disabled"));
                plugin.getMessagingService().sendStaffChatToggle(user, user.isStaffChat());
                return;
            }
        }

        StringBuilder message = new StringBuilder(args[0]);
        Arrays.stream(args).skip(1).forEach(str -> message.append(" ").append(str));

        String wholeMessage = plugin.getMessage("StaffChat-Module.StaffChat-Message")
                .replace("%server%", player == null ? "Void" : player.getServer().getInfo().getName())
                .replace("%player%", player == null ? "Console" : player.getName())
                .replace("%prefix%", player == null ? "&6Mighty &e" : plugin.getPrefix(player))
                .replace("%rank%", player == null ? "System" : user.getRank().getName())
                .replace("%message%", message.toString());

        plugin.getStaffManager().sendMessage(wholeMessage, MessageType.STAFF_MESSAGE);
    }
}
