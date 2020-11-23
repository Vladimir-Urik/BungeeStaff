package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class JoinListener extends EventListener {

    public JoinListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        StaffUser user = plugin.getStaffManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        // 1 ms delay?
        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            user.setOnline(true);
        }, 1, TimeUnit.MILLISECONDS);

        if (!plugin.hasCustomPermission("Staff-Join", player))
            return;

        for (ProxiedPlayer loopPlayer : ProxyServer.getInstance().getPlayers()) {

            if (!plugin.hasCustomPermission("Staff-Join", loopPlayer))
                continue;

            user.setName(player.getName());

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null || !loopUser.isStaffChat())
                continue;

            String prefix = user.getRank() == null ? plugin.getConfig().getString("No-Rank") : user.getRank().getPrefix();

            TextUtil.sendMessage(loopPlayer, plugin.getMessages().getString("Staff-Messages.Staff-Join")
                    .replace("%player%", player.getName())
                    .replace("%prefix%", prefix));
        }
    }
}
