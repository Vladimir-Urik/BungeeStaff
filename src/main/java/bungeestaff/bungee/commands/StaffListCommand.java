package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.TextUtil;
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

        StringBuilder header = new StringBuilder(plugin.getMessage("Staff-List.Header"));

        for (StaffUser user : onlineStaff) {
            String line = plugin.getMessage("Staff-List.List")
                    .replace("%rank%", user.getRank().getName())
                    .replace("%player%", user.getName())
                    .replace("%prefix%", plugin.getPrefix(user.getUniqueID()))
                    .replace("%server%", user.getServer());
            header.append("\n").append(line);
        }

        header.append("\n").append(plugin.getMessage("Staff-List.Footer"));

        TextUtil.sendMessage(sender, header.toString()
                .replace("%online_staff%", String.valueOf(onlineStaff.size())));
    }
}
