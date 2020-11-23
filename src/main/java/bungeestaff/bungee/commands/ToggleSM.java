package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.File;
import java.io.IOException;

public class ToggleSM extends Command {

    public ToggleSM() {
        super("togglestaffmessages", "", "tsm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;

            if(sender.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Toggle-Staff-Messages"))) {
                if(BungeeStaffPlugin.getInstance().getSettings().getBoolean("Settings." + p.getUniqueId() + ".Staff-Messages") == true) {
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "settings.yml");
                        p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Messages-Off")));
                        BungeeStaffPlugin.getInstance().getSettings().set("Settings." + p.getUniqueId() + ".Staff-Messages", false);
                        BungeeStaffPlugin.getInstance().settingsPP.save(BungeeStaffPlugin.getInstance().settings, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        File file = new File(BungeeStaffPlugin.getInstance().getDataFolder(), "settings.yml");
                        p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-Messages.Staff-Messages-On")));
                        BungeeStaffPlugin.getInstance().getSettings().set("Settings." + p.getUniqueId() + ".Staff-Messages", true);
                        BungeeStaffPlugin.getInstance().settingsPP.save(BungeeStaffPlugin.getInstance().settings, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("No-Permission")));
            }
        }
    }
}
