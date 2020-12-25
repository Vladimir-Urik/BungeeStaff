package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffFollowCommand extends CommandBase {

    public StaffFollowCommand(BungeeStaffPlugin plugin) {
        super(plugin, "bstafffollow", "StaffChat-Follow");
        setPlayerOnly(true);
        withRange(1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        CachedUser target = plugin.getUserCache().getUser(args[0]);

        if (target == null) {
            plugin.sendMessage(player, "Report-Module.Player-Not-Found");
            return;
        }

        ServerInfo targetServer = plugin.getProxy().getServerInfo(target.getServer());

        if (targetServer.getPlayers().contains(player)) {
            TextUtil.sendMessage(player, plugin.getMessage("Staff-Follow.Already-In")
                    .replace("%player%", target.getName()));
        } else {
            TextUtil.sendMessage(player, plugin.getMessage("Staff-Follow.Joining")
                    .replace("%target%", target.getName())
                    .replace("%target_server%", targetServer.getName()));

            player.connect(targetServer);
        }
    }
}
