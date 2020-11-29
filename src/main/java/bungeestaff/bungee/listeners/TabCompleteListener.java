package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.event.EventHandler;

public class TabCompleteListener extends EventListener {

    public TabCompleteListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String partialName;

        if (event.isCancelled())
            return;

        String cursor = event.getCursor().toLowerCase();

        partialName = cursor.lastIndexOf(32) >= 0 ? cursor.substring(cursor.lastIndexOf(32) + 1) : cursor;

        for (CachedUser player : plugin.getUsers()) {
            String playerName = player.getName();
            if (playerName.toLowerCase().startsWith(partialName) && !event.getSuggestions().contains(playerName))
                event.getSuggestions().add(playerName);
        }
    }
}
