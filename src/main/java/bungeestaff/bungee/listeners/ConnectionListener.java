package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;

public class ConnectionListener extends EventListener {

    public ConnectionListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        // Switch and join messages
        for (ProxiedPlayer loopPlayer : plugin.getProxy().getPlayers()) {

            if (!plugin.hasCustomPermission("Server-Switch", player) || !plugin.hasCustomPermission("Server-Switch-Notify", loopPlayer))
                continue;

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null || !loopUser.isStaffChat())
                continue;

            String prefix = user.getRank() == null ? plugin.getConfig().getString("No-Rank") : user.getRank().getPrefix();

            ServerInfo targetServer = event.getTarget();

            // Why a return when he's already online..? some kind of back check?
            if (targetServer.getPlayers().contains(player))
                return;

            if (player.getServer() == null) {
                TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.First-Join")
                        .replace("%player%", player.getName())
                        .replace("%server%", targetServer.getName())
                        .replace("%prefix%", prefix));
            } else {
                TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.Switch")
                        .replace("%player%", player.getName())
                        .replace("%server_to%", targetServer.getName())
                        .replace("%server_from%", player.getServer().getInfo().getName())
                        .replace("%prefix%", prefix));
            }
        }
    }
}
