package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.util.TextUtil;
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
        ProxiedPlayer player = (ProxiedPlayer) sender;

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        if (args.length == 0) {
            plugin.sendLineMessage("Staff-Chat-Module.StaffChat-" + (user.switchStaffChat() ? "Enabled" : "Disabled"), player);
            return;
        }

        StringBuilder message = new StringBuilder();
        Arrays.stream(args).forEach(message::append);

        String wholeMessage = plugin.getLineMessage("StaffChat-Module.StaffChat-Message")
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%player%", player.getName())
                .replace("%message%", message.toString())
                .replace("%prefix%", plugin.getPrefix(player));

        for (ProxiedPlayer loopPlayer : plugin.getProxy().getPlayers()) {

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null || !loopUser.isOnline() || !loopUser.isStaffMessages())
                continue;

            TextUtil.sendMessage(loopPlayer, wholeMessage);
        }

        TextUtil.sendMessage(plugin.getProxy().getConsole(), wholeMessage);
    }
}
