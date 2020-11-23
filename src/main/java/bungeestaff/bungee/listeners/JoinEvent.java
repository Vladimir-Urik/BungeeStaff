package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JoinEvent extends EventListener {

    public JoinEvent(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        // 1 ms delay?
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            Configuration conf = BungeeStaffPlugin.getInstance().getConfig().getSection("Ranks");

            for (String key : conf.getKeys()) {

                if (conf.getSection(key).getStringList("users").contains(player.getUniqueId().toString())) {
                    Data.onlinestaff.put(player.getName(), key);

                    String rank = Data.onlinestaff.get(player.getName());
                    String prefix = BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix");

                    Data.prefix.put(player.getName(), prefix);
                }
            }
        }, 1, TimeUnit.MILLISECONDS);

        for (ProxiedPlayer loopPlayer : ProxyServer.getInstance().getPlayers()) {
            if (loopPlayer.hasPermission(plugin.getConfig().getString("Custom-Permissions.Staff-Join"))) {
                if (player.hasPermission(plugin.getConfig().getString("Custom-Permissions.Staff-Join"))) {

                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "settings.yml");
                        BungeeStaffPlugin.getInstance().getSettings().set("Settings." + player.getUniqueId() + ".Username", player.getName());
                        BungeeStaffPlugin.getInstance().settingsPP.save(BungeeStaffPlugin.getInstance().settings, file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    if (BungeeStaffPlugin.getInstance().getSettings().get("Settings." + player.getUniqueId() + ".Staff-Messages") == null) {
                        try {
                            File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "settings.yml");
                            BungeeStaffPlugin.getInstance().getSettings().set("Settings." + player.getUniqueId() + ".Staff-Messages", true);
                            BungeeStaffPlugin.getInstance().settingsPP.save(BungeeStaffPlugin.getInstance().settings, file);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + loopPlayer.getUniqueId() + ".Staff-Messages") == true) {
                        if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Use-Maintenance") == true) {
                            if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Enabled") == true) {
                                List<String> users = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");
                                if (users.contains(player.getName().toLowerCase())) {
                                    if (Data.prefix.containsKey(player.getName())) {
                                        String rank = Data.onlinestaff.get(player.getName());
                                        loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                                .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                                    } else {
                                        loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                                .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                                    }
                                }
                            } else {
                                if (Data.prefix.containsKey(player.getName())) {
                                    String rank = Data.onlinestaff.get(player.getName());
                                    loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                                } else {
                                    loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                                }
                            }
                        } else {
                            if (Data.prefix.containsKey(player.getName())) {
                                String rank = Data.onlinestaff.get(player.getName());
                                loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                        .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                            } else {
                                loopPlayer.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Join").replaceAll("%player%", player.getName()))
                                        .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                            }
                        }
                    }
                }
            }
        }
    }
}
