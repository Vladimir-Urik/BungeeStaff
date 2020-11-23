package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Data;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffList extends Command {

    public StaffList() {
        super("stafflist", "", "slist", "staff");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (sender.hasPermission(BungeeStaffPlugin.getInstance().getConfig().getString("Custom-Permissions.Staff-List"))) {
                for (String msg : BungeeStaffPlugin.getInstance().getMessages().getStringList("Staff-List.Header")) {
                    p.sendMessage(BungeeStaffPlugin.getInstance().translate(msg.replaceAll("%online_staff%", String.valueOf(Data.onlinestaff.size()))));
                }
                for (String key : Data.onlinestaff.keySet()) {
                    ProxiedPlayer staff = ProxyServer.getInstance().getPlayer(key);

                    String format = BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-List.List").replaceAll("%player%", staff.getName()))
                            .replaceAll("%prefix%", BungeeStaffPlugin.getInstance().translate(Data.prefix.get(staff.getName())))
                            .replaceAll("%player_server%", staff.getServer().getInfo().getName());

                    p.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("Staff-List.Dont-Edit").replaceAll("%list%", format)));
                }
                for(String oofsa : BungeeStaffPlugin.getInstance().getMessages().getStringList("Staff-List.Footer")) {
                    p.sendMessage(BungeeStaffPlugin.getInstance().translate(oofsa.replaceAll("%online_staff%", String.valueOf(Data.onlinestaff.size()))));
                }
            } else {
                sender.sendMessage(BungeeStaffPlugin.getInstance().translate(BungeeStaffPlugin.getInstance().getMessages().getString("No-Permission")));
            }
        }
    }
}
