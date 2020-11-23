package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MaintenanceCMD extends Command {

    public MaintenanceCMD() {
        super("maintenance", "");
    }

    public void execute(CommandSender sender, String[] args) {

        if (sender.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Maintenance-Command"))) {
            if (args.length == 0) {
                for (String string : BungeeStaffPlugin.getInstance().getMessages().getStringList("Maintenance-Module.No-Argument")) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(string));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("help")) {
                for (String help : BungeeStaffPlugin.getInstance().getMessages().getStringList("Maintenance-Module.Help")) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(help));
                }
            } else if (args[0].equalsIgnoreCase("on")) {
                if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Enabled") == true) {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Already-Enabled")));
                } else {
                    if (BungeeStaffPlugin.getInstance().getMessages().getBoolean("Maintenance-Module.M-On.Broadcast-Staff") == true) {
                        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                            if (pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Maintenance-Notify"))) {
                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.M-On.Message")).replaceAll("%player%", sender.getName()));
                            }
                        }
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.M-On.Message")).replaceAll("%player%", sender.getName()));
                    }
                    for (ProxiedPlayer pp : BungeeCord.getInstance().getPlayers()) {
                        if (!pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Maintenance-Bypass"))) {
                            pp.disconnect(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Online-Kick")).replaceAll("%NEWLINE%", "\n"));
                        }
                    }
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                        BungeeStaffPlugin.getInstance().getConfig().set("Maintenance.Enabled", true);
                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (args[0].equalsIgnoreCase("off")) {
                if (BungeeStaffPlugin.getInstance().getConfig().getBoolean("Maintenance.Enabled") == true) {
                    if (BungeeStaffPlugin.getInstance().getMessages().getBoolean("Maintenance-Module.M-Off.Broadcast-Staff") == true) {
                        for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                            if (pp.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Maintenance-Notify"))) {
                                pp.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.M-Off.Message")).replaceAll("%player%", sender.getName()));
                            } else {
                                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.M-Off.Message")).replaceAll("%player%", sender.getName()));
                            }
                        }
                    }
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                        BungeeStaffPlugin.getInstance().getConfig().set("Maintenance.Enabled", false);
                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Already-Disabled")));
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                for (String message : BungeeStaffPlugin.getInstance().getMessages().getStringList("Maintenance-Module.List")) {
                    List<String> lp = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");
                    sender.sendMessage(BungeeStaffPlugin.getInstance().translate(message.replaceAll("%players%", String.valueOf(lp).replaceAll("\\[", "").replaceAll("]", ""))));
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 1) {
                    for (String string : BungeeStaffPlugin.getInstance().getMessages().getStringList("Maintenance-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(string));
                    }
                    return;
                }
                String username = args[1].toLowerCase();
                List<String> whitelisted = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");
                File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                try {
                    if (whitelisted.contains(username)) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Player-Already-Whitelisted").replaceAll("%arg%", username)));
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Player-Add-Whitelist").replaceAll("%arg%", username)));
                        whitelisted.add(username);
                        BungeeStaffPlugin.getInstance().getConfig().set("Maintenance.Whitelisted-Players", whitelisted);
                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 1) {
                    for (String string : BungeeStaffPlugin.getInstance().getMessages().getStringList("Maintenance-Module.No-Argument")) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(string));
                    }
                    return;
                }
                String username = args[1].toLowerCase();
                List<String> whitelisted = BungeeStaffPlugin.getInstance().getConfig().getStringList("Maintenance.Whitelisted-Players");
                File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "config.yml");
                try {
                    if (whitelisted.contains(username)) {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Player-Remove-Whitelist").replaceAll("%arg%", username)));
                        whitelisted.remove(username);
                        BungeeStaffPlugin.getInstance().getConfig().set("Maintenance.Whitelisted-Players", whitelisted);
                        BungeeStaffPlugin.getInstance().bungeestaffPP.save(BungeeStaffPlugin.getInstance().bungeestaff, file);
                    } else {
                        sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Maintenance-Module.Player-Not-Whitelisted").replaceAll("%arg%", username)));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("No-Permission")));
        }
    }
}
