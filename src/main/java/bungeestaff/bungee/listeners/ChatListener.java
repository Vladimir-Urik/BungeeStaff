package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
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

        Rank rank = user.getRank();

        String message = plugin.getMessages().getString("StaffChat-Module.StaffChat-Message")
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%player%", player.getName())
                .replace("%message%", event.getMessage());

        // Send one to console
        TextUtil.sendMessage(plugin.getProxy().getConsole(), message
                .replace("%prefix%", rank == null ? plugin.getConfig().getString("No-Rank") : rank.getPrefix()));

        event.setCancelled(true);

        for (ProxiedPlayer loopPlayer : ProxyServer.getInstance().getPlayers()) {

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null)
                continue;

            if (!plugin.hasCustomPermission("StaffChat-Notify-Command") || !loopUser.isStaffChat())
                continue;

            String prefix = user.getRank() == null ? plugin.getConfig().getString("No-Rank") : user.getRank().getPrefix();
            TextUtil.sendMessage(loopPlayer, message.replace("%prefix%", prefix));
        }
    }
}
