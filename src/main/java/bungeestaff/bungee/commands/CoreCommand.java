package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.rabbit.cache.CachedUser;
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
                .withExecutor((sender, args) -> plugin.sendMessage(sender, "BungeeStaff-Module.Help"))
                .withRange(0);

        withSubCommand("reload")
                .withExecutor((sender, args) -> plugin.reload(sender))
                .withRange(0);

        withSubCommand("add")
                .withExecutor((sender, args) -> {

                    CachedUser user;
                    ProxiedPlayer target = plugin.getProxy().getPlayer(args[0]);

                    if (target == null) {
                        user = plugin.getMessagingService().getUserCache().getUser(args[0]);

                        if (user == null) {
                            plugin.sendMessage(sender, "General.Player-Offline");
                            return;
                        }
                    } else user = new CachedUser(target);

                    Rank rank = plugin.getRankManager().getRank("default");
                    if (args.length > 1) {
                        rank = plugin.getRankManager().getRank(args[1]);

                        if (rank == null) {
                            plugin.sendMessage(sender, "General.Invalid-Rank");
                            return;
                        }
                    }

                    plugin.getStaffManager().addUser(user, rank, true);
                    plugin.sendMessage(sender, "BungeeStaff-Module.User-Added");
                })
                .withRange(1, 2);

        withSubCommand("remove")
                .withExecutor((sender, args) -> {
                    StaffUser user = plugin.getStaffManager().getUser(args[0]);

                    if (user == null) {
                        plugin.sendMessage(sender, "General.Invalid-User");
                        return;
                    }

                    plugin.getStaffManager().removeUser(user, true);
                    plugin.sendMessage(sender, "BungeeStaff-Module.User-Removed");
                })
                .withRange(1);

        withSubCommand("ranks")
                .withAliases("list")
                .withExecutor((sender, args) -> {
                    StringBuilder message = new StringBuilder("&8&m          &3 Ranks &8&m          ");
                    for (Rank rank : plugin.getRankManager().getRanks()) {
                        Set<StaffUser> users = plugin.getStaffManager().getUsers(u -> rank.equals(u.getRank()));
                        message.append("\n&8 - &e").append(rank.getName()).append("&8: ");
                        if (!users.isEmpty()) {
                            message.append(TextUtil.joinStream("&7, ", users.stream()
                                    // Map StaffUser to name and color based on props
                                    .map(u -> {
                                        String pref = "&f";
                                        if (u.isOnline())
                                            pref = "&a";
                                        if (u.isStaffMessages())
                                            pref += "&n";
                                        return pref + u.getName();
                                    }), n -> n));
                        } else message.append("&cnone");
                    }
                    TextUtil.sendMessage(sender, message.toString());
                })
                .withRange(0);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        plugin.sendMessage(sender, "BungeeStaff-Module.Help");
    }
}
