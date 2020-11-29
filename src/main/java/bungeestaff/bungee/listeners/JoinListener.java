package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

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

        user.setOnline(true);
        user.setName(player.getName());

        if (!plugin.hasCustomPermission("Staff-Join", player))
            return;

        plugin.getStaffManager().sendRawMessage(plugin.getMessages().getString("Staff-Messages.Staff-Join")
                        .replace("%player%", player.getName())
                        .replace("%prefix%", plugin.getPrefix(player)), MessageType.JOIN);
    }
}
