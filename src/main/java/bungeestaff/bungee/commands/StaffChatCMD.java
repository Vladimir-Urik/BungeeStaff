package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChatCMD extends Command {

    public StaffChatCMD() {
        super("staffchat", "", "sc");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {
            if(sender.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.StaffChat-Command"))) {
                ProxiedPlayer p = (ProxiedPlayer) sender;

                if(args.length == 0) {
                    if(BungeeStaffPlugin.getInstance().getStaffChat().contains(p)) {
                        BungeeStaffPlugin.getInstance().staffChat.remove(p);
                        p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("StaffChat-Module.StaffChat-Disabled")));
                    } else {
                        BungeeStaffPlugin.getInstance().staffChat.add(p);
                        p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("StaffChat-Module.StaffChat-Enabled")));
                    }
                    return;
                }
                if(args.length >= 1) {
                    StringBuilder ss = new StringBuilder();
                    for(int i = 0; i < args.length; i++) {
                        ss.append(args[i]).append(" ");
                    }
                    for(ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                        if(pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Request-Notify"))) {
                            if(Data.prefix.containsKey(p.getName())) {
                                net.md_5.bungee.config.Configuration s = BungeeStaffPlugin.getInstance().getConfig().getSection("Ranks");

                                for(String key : s.getKeys()) {
                                    s.get(key);

                                    for (String oof : s.getSection(key).getStringList("users")) {
                                        if (oof.contains(p.getUniqueId().toString())) {
                                            if (BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + pp.getUniqueId() + ".Staff-Messages") == true) {
                                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("StaffChat-Module.StaffChat-Message"))
                                                        .replaceAll("%player_server%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()).replaceAll("%message%", ss.toString())
                                                        .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(s.getSection(key).getString("prefix"))));
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + pp.getUniqueId() + ".Staff-Messages") == true) {
                                    pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("StaffChat-Module.StaffChat-Message"))
                                            .replaceAll("%player_server%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()).replaceAll("%message%", ss.toString())
                                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("No-Rank"))));
                                }
                            }
                        }
                    }
                    String rank = Data.onlinestaff.get(p.getName());

                    ProxyServer.getInstance().getConsole().sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("StaffChat-Module.StaffChat-Message"))
                            .replaceAll("%player_server%", p.getServer().getInfo().getName()).replaceAll("%player%", p.getName()).replaceAll("%message%", ss.toString())
                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getConfig().getString("Ranks." + rank + ".prefix"))));
                    return;
                }

            } else {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("No-Permission")));
            }
        }
    }
}
