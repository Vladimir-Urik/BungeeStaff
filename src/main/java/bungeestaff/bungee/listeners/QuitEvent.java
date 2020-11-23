package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class QuitEvent implements Listener {

    public QuitEvent() {
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeStaffPlugin.getInstance(), this);
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
            if (pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Staff-Leave"))) {
                if (p.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Staff-Leave"))) {
                    if (BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + pp.getUniqueId() + ".Staff-Messages") == true) {
                        if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Use-Maintenance") == true) {
                            if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Enabled") == true) {
                                List<String> users = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");
                                if (users.contains(p.getName().toLowerCase())) {
                                    if (Data.prefix.containsKey(p.getName())) {
                                        String rank = Data.onlinestaff.get(p.getName());
                                        pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                                .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                                    } else {
                                        pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                                .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                                    }
                                }
                            } else {
                                if (Data.prefix.containsKey(p.getName())) {
                                    String rank = Data.onlinestaff.get(p.getName());
                                    pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                                } else {
                                    pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                                }
                            }
                        } else {
                            if (Data.prefix.containsKey(p.getName())) {
                                String rank = Data.onlinestaff.get(p.getName());
                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                        .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                            } else {
                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Leave").replaceAll("%server_from%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()))
                                        .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                            }
                        }
                    }
                }
            }
        }

        BungeeStaffPlugin.getInstance().staffChat.remove(p);
        BungeeStaffPlugin.getInstance().requestcooldown.remove(p);
        BungeeStaffPlugin.getInstance().reportcooldown.remove(p);
        BungeeStaffPlugin.getInstance().staffonline.remove(p);

        Data.prefix.remove(p.getName());
        Data.onlinestaff.remove(p.getName());
    }
}
