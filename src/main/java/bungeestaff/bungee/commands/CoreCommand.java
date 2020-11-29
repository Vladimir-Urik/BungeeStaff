package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

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

        withSubCommand("add")
                .withExecutor((sender, args) -> {
                    ProxiedPlayer player = plugin.getProxy().getPlayer(args[0]);

                    if (player == null) {
                        plugin.sendLineMessage("General.Player-Offline", sender);
                        return;
                    }

                    Rank rank = plugin.getRankManager().getRank("default");
                    if (args.length > 1) {
                        rank = plugin.getRankManager().getRank(args[1]);

                        if (rank == null) {
                            plugin.sendLineMessage("General.Invalid-Rank", sender);
                            return;
                        }
                    }

                    plugin.getStaffManager().addUser(player, rank);
                    plugin.sendLineMessage("BungeeStaff-Module.User-Added", sender);
                })
                .withRange(1, 2);

        withSubCommand("remove")
                .withExecutor((sender, args) -> {
                    StaffUser user = plugin.getStaffManager().getUser(args[0]);

                    if (user == null) {
                        plugin.sendLineMessage("General.Invalid-User", sender);
                        return;
                    }

                    plugin.getStaffManager().removeUser(user);
                    plugin.sendLineMessage("BungeeStaff-Module.User-Removed", sender);
                })
                .withRange(1);

        withSubCommand("ranks")
                .withExecutor((sender, args) -> {
                    StringBuilder message = new StringBuilder("&8&m          &3 Ranks &8&m          ");
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
