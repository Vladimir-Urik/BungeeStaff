package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import net.md_5.bungee.api.plugin.Listener;

public abstract class EventListener implements Listener {

    protected final BungeeStaffPlugin plugin;

    public EventListener(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }
}
