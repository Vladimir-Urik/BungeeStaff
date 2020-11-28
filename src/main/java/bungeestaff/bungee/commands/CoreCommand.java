package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.util.TextUtil;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;

import java.util.Set;

public class CoreCommand extends CommandBase {

    public CoreCommand(BungeeStaffPlugin plugin) {
        super(plugin, "bungeestaff", "Core-Command", "bstaff");

        setRange(-1);

        withSubCommand("help")
                .withExecutor((sender, args) -> plugin.sendListMessage("BungeeStaff-Module.Help", sender))
                .withRange(0);

        withSubCommand("reload")
                .withExecutor((sender, args) -> plugin.reload(sender))
                .withRange(0);

        withSubCommand("list")
                .withExecutor((sender, args) -> {
                    StringBuilder message = new StringBuilder("&8&m        &3 Rank list &8&m        ");
                    for (Rank rank : plugin.getRankManager().getRanks()) {
                        Set<StaffUser> users = plugin.getStaffManager().getUsers(u -> rank.equals(u.getRank()));
                        message.append("\n&8 - &e").append(rank.getName());
                        users.forEach(u -> message.append("\n&8  - &f").append(u.getName()));
                    }
                    TextUtil.sendMessage(sender, message.toString());
                })
                .withRange(0);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        plugin.sendListMessage("BungeeStaff-Module.Help", sender);
    }
}
