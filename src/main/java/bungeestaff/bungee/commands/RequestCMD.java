package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class RequestCMD extends Command {
    public RequestCMD() {
        super("request", "", "helpop");
    }

    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (args.length == 0) {
                for (String s : BungeeStaffPlugin.getInstance().getMessages().getStringList("Request-Module.No-Argument")) {
                    p.sendMessage(BungeeStaffPlugin.getInstance().translate(s));
                }
                return;
            }
            if (args.length >= 1) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    s.append(args[i]).append(" ");
                }
                if (BungeeStaffPlugin.getInstance().requestcooldown.contains(p)) {
                    p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Request-Module.Request-Cooldown-Message"))
                            .replaceAll("%amount%", String.valueOf(BungeeStaffPlugin.getInstance().getMessages().getInt("Request-Module.Request-Cooldown"))).replaceAll("%type%", BungeeStaffPlugin.getInstance().getMessages().getString("Request-Module.Request-Cooldown-Type")));
                    return;
                }
                BungeeStaffPlugin.getInstance().requestcooldown.add(p);
                p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Request-Module.Request-Sent")));

                for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                    if (pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Request-Notify"))) {
                        for (String string : BungeeStaffPlugin.getInstance().getMessages().getStringList("Request-Module.Request-Broadcast")) {
                            if (BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + pp.getUniqueId() + ".Staff-Messages") == true) {
                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(string).replaceAll("%player_server%", p.getServer().getInfo().getName())
                                        .replaceAll("%player%", p.getName()).replaceAll("%reason%", s.toString()));
                            }
                        }
                    }
                    ProxyServer.getInstance().getScheduler().schedule(BungeeStaffPlugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            BungeeStaffPlugin.getInstance().requestcooldown.remove(p);
                        }
                    }, BungeeStaffPlugin.getInstance().getMessages().getInt("Request-Module.Request-Cooldown"), TimeUnit.valueOf(BungeeStaffPlugin.getInstance().getMessages().getString("Request-Module.Request-Cooldown-Type").toUpperCase()));
                }
            }
        }
    }
}
