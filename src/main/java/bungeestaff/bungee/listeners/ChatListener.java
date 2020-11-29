package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChatListener extends EventListener {

    public ChatListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        StaffUser user = plugin.getStaffManager().getUser(player);

        if (user == null || !user.isStaffChat() || event.getMessage().startsWith("/"))
            return;

        event.setCancelled(true);

        plugin.getStaffManager().sendStaffMessage(user, event.getMessage());
    }
}
