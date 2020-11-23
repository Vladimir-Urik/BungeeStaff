package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class ConnectionListener extends EventListener {

    public ConnectionListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        // Maintenance
        if (plugin.getConfig().getBoolean("Maintenance.Use-Maintenance") &&
                plugin.getConfig().getBoolean("Maintenance.Enabled") &&
                !player.hasPermission(plugin.getConfig().getString("Custom-Permissions.Maintenance-Bypass"))) {

            List<String> whitelist = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");

            if (!whitelist.contains(player.getName().toLowerCase())) {
                event.setCancelled(true);
                String disconnectMessage = plugin.getMessages().getString("Maintenance-Module.Join-Message");

                player.disconnect(TextUtil.format(disconnectMessage.replaceAll("%server%", event.getTarget().getName())
                        .replaceAll("%NEWLINE%", "\n")));
            }
        }

        // Switch and join messages
        for (ProxiedPlayer loopPlayer : plugin.getProxy().getPlayers()) {

            if (!player.hasPermission(plugin.getConfig().getString("Custom-Permissions.Server-Switch")) ||
                    !loopPlayer.hasPermission(plugin.getConfig().getString("Custom-Permissions.Server-Switch-Notify"))) {
                continue;
            }

            if (!BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + loopPlayer.getUniqueId() + ".Staff-Messages")) {
                continue;
            }

            StaffUser user = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (user != null && user.isOnline()) {
                String targetServer = event.getTarget().getName();

                if (Data.prefix.containsKey(player.getName())) {
                    if (event.getTarget().getPlayers().contains(player)) {
                        return;
                    } else {
                        String rank = Data.onlinestaff.get(player.getName());
                        if (player.getServer() == null) {
                            TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.First-Join")
                                    .replace("%player%", player.getName())
                                    .replace("%server%", targetServer)
                                    .replace("%prefix%", plugin.getConfig().getString("Ranks." + rank + ".prefix")));
                        } else {
                            TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.Switch")
                                    .replace("%player%", player.getName())
                                    .replace("%server_to%", targetServer)
                                    .replace("%server_from%", player.getServer().getInfo().getName())
                                    .replace("%prefix%", plugin.getConfig().getString("Ranks." + rank + ".prefix")));
                        }
                    }
                }
            } else {
                if (player.getServer() == null) {
                    TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.First-Join")
                            .replace("%player%", player.getName())
                            .replace("%server%", event.getTarget().getName())
                            .replace("%prefix%", plugin.getConfig().getString("No-Rank")));
                } else {
                    TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Server-Switch-Module.Switch")
                            .replace("%player%", player.getName())
                            .replace("%server_to%", event.getTarget().getName())
                            .replace("%server_from%", player.getServer().getInfo().getName())
                            .replace("%prefix%", plugin.getConfig().getString("No-Rank")));
                }
            }
        }
    }
}
