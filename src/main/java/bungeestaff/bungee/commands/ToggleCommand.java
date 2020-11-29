package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ToggleCommand extends CommandBase {

    public ToggleCommand(BungeeStaffPlugin plugin) {
        super(plugin, "togglestaffmessages", "Toggle-Staff-Messages", "tsm");
        setPlayerOnly(true);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        StaffUser user = plugin.getUser(player);

        if (user == null)
            return;

        plugin.sendLineMessage("Staff-Messages.Staff-Messages-" + (user.switchStaffMessages() ? "On" : "Off"), player);
    }
}
