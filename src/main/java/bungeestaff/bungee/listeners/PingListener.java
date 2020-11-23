package bungeestaff.bungee.listeners;

import bungeestaff.bungee.BungeeStaffPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener extends EventListener {

    public PingListener(BungeeStaffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Use-Maintenance") == true) {
            if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Enabled") == true) {
                ServerPing serverPing = e.getResponse();
                ServerPing.Protocol protocol = serverPing.getVersion();
                protocol.setProtocol(2);
                protocol.setName(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.ServerList")));
            }
        }
    }
}
