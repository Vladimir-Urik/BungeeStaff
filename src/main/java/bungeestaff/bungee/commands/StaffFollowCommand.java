package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.util.TextUtil;
import bungeestaff.bungee.commands.framework.CommandBase;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            plugin.sendLineMessage("Report-Module.Player-Not-Found", player);
            return;
        }

        ServerInfo targetServer = target.getServer().getInfo();

        if (targetServer.getPlayers().contains(player)) {
            TextUtil.sendMessage(player, plugin.getLineMessage("Staff-Follow.Already-In")
                    .replace("%player%", target.getName()));
        } else {
            TextUtil.sendMessage(player, plugin.getLineMessage("Staff-Follow.Joining")
                    .replace("%target%", target.getName())
                    .replace("%target_server%", target.getServer().getInfo().getName()));

            player.connect(targetServer);
        }
    }
}
