package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.system.staff.StaffUser;
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

        StaffUser user = plugin.getStaffManager().getUser(player);

        if (user == null)
            return;

        user.setOnline(true);
        user.setName(player.getName());

        // Delay 1 second so the server is not null.
        plugin.getProxy().getScheduler().schedule(plugin, () -> plugin.getMessagingService().getUserCache().addUser(player), 1, TimeUnit.SECONDS);

        plugin.getMessagingService().sendStaffJoin(user);

        plugin.getStaffManager().sendMessage(plugin.getMessages().getString("Staff-Messages.Staff-Join")
                .replace("%player%", player.getName())
                .replace("%prefix%", plugin.getPrefix(player)), MessageType.STAFF);
    }
}
