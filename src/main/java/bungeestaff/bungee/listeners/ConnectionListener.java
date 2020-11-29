package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.MessageType;
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

        StaffUser user = plugin.getStaffManager().getUser(player);

        if (user == null)
            return;

        ServerInfo targetServer = event.getTarget();

        // Why a return when he's already online..? some kind of back check?
        if (targetServer.getPlayers().contains(player))
            return;

        String prefix = plugin.getPrefix(player);

        String message;

        if (player.getServer() == null) {
            message = plugin.getLineMessage("Server-Switch-Module.First-Join")
                    .replace("%player%", player.getName())
                    .replace("%server%", targetServer.getName())
                    .replace("%prefix%", prefix);
        } else {
            message = plugin.getLineMessage("Server-Switch-Module.Switch")
                    .replace("%player%", player.getName())
                    .replace("%server_to%", targetServer.getName())
                    .replace("%server_from%", player.getServer().getInfo().getName())
                    .replace("%prefix%", prefix);
        }

        plugin.getStaffManager().sendRawMessage(message, MessageType.STAFF);
    }
}
