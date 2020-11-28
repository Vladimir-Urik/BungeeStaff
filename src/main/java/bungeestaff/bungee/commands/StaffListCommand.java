package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.util.TextUtil;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;

import java.util.Set;

public class StaffListCommand extends CommandBase {

    public StaffListCommand(BungeeStaffPlugin plugin) {
        super(plugin, "stafflist", "Staff-List", "slist", "staff");
        withRange(0);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Set<StaffUser> onlineStaff = plugin.getStaffManager().getUsers(StaffUser::isOnline);

        StringBuilder header = new StringBuilder(plugin.getListMessage("Staff-List.Header"));

        for (StaffUser user : onlineStaff) {
            String line = plugin.getLineMessage("Staff-List.List")
                    .replace("%prefix%", plugin.getPrefix(user.getUniqueID()))
                    .replace("%server%", user.asPlayer().getServer().getInfo().getName());
            header.append(line);
        }

        header.append(plugin.getListMessage("Staff-List.Footer"));

        TextUtil.sendMessage(sender, header.toString()
                .replace("%online_staff%", String.valueOf(onlineStaff.size())));
    }
}
