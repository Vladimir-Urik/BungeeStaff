package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.util.TextUtil;
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

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        if (!user.isStaffChat() || event.getMessage().startsWith("/"))
            return;

        String prefix = plugin.getPrefix(player);

        String wholeMessage = plugin.getMessages().getString("StaffChat-Module.StaffChat-Message")
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%player%", player.getName())
                .replace("%message%", event.getMessage())
                .replace("%prefix%", prefix);

        // Send one to console
        TextUtil.sendMessage(plugin.getProxy().getConsole(), wholeMessage);

        event.setCancelled(true);

        for (ProxiedPlayer loopPlayer : ProxyServer.getInstance().getPlayers()) {

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null)
                continue;

            if (!plugin.hasCustomPermission("StaffChat-Notify-Command") || !loopUser.isStaffMessages())
                continue;

            TextUtil.sendMessage(loopPlayer, wholeMessage);
        }
    }
}
