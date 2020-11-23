package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class ChatListener extends EventListener {

    public ChatListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (!plugin.getStaffManager().hasChatEnabled(player.getUniqueId()) || event.getMessage().startsWith("/"))
            return;

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        String rank = user.getRank();

        String message = plugin.getMessages().getString("StaffChat-Module.StaffChat-Message")
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%player%", player.getName())
                .replace("%message%", event.getMessage());

        // Send one to console
        TextUtil.sendMessage(plugin.getProxy().getConsole(), message.replace("%prefix%", plugin.getConfig().getString("Ranks." + rank + ".prefix")));

        event.setCancelled(true);

        for (ProxiedPlayer loopPlayer : ProxyServer.getInstance().getPlayers()) {

            if (!loopPlayer.hasPermission(plugin.getConfig().getString("Custom-Permissions.StaffChat-Notify-Command")) ||
                    !plugin.getSettings().getBoolean("Settings." + loopPlayer.getUniqueId() + ".Staff-Messages"))
                continue;

            if (!Data.prefix.containsKey(player.getName())) {
                TextUtil.sendMessage(loopPlayer, message.replaceAll("%prefix%", plugin.getConfig().getString("No-Rank")));
                continue;
            }

            //TODO preload
            Configuration conf = BungeeStaffPlugin.getInstance().getConfig().getSection("Ranks");

            for (String key : conf.getKeys()) {
                conf.get(key);

                if (conf.getSection(key).getStringList("users").contains(player.getUniqueId().toString())) {
                    TextUtil.sendMessage(loopPlayer, message.replaceAll("%prefix%", conf.getSection(key).getString("prefix")));
                }
            }
        }
    }
}
