package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BroadcastCMD extends Command {

    public BroadcastCMD() {
        super("broadcast", "", "announce");
    }

    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (sender.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Broadcast-Command"))) {
                if (args.length == 0) {
                    for (String noarg : BungeeStaffPlugin.getInstance().getMessages().getStringList("Broadcast-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(noarg));
                    }
                    return;
                }
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    s.append(args[i]).append(" ");
                }
                for (String message : BungeeStaffPlugin.getInstance().getMessages().getStringList("Broadcast-Module.Message")) {
                    for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                        if (Data.prefix.containsKey(p.getName())) {
                            String rank = Data.onlinestaff.get(p.getName());
                            pp.sendMessage(BungeeStaffPlugin.getInstance().translate(message.replaceAll("%player%", sender.getName())
                                    .replaceAll("%player_server%", p.getServer().getInfo().getName()).replaceAll("%message%", s.toString()))
                                    .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                        } else {
                            pp.sendMessage(BungeeStaffPlugin.getInstance().translate(message.replaceAll("%player%", sender.getName())
                                    .replaceAll("%player_server%", p.getServer().getInfo().getName()).replaceAll("%message%", s.toString()))
                                    .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                        }
                    }
                }
            } else {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("No-Permission")));
            }
        }
    }
}
