package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.cooldown.CooldownType;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RequestCommand extends CommandBase {

    public RequestCommand(BungeeStaffPlugin plugin) {
        super(plugin, "request", "", "helpop");
        setPlayerOnly(true);
        withRange(1, -1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 2)
            return;

        StringBuilder message = new StringBuilder();
        Arrays.stream(args).skip(1).forEach(message::append);

        if (!plugin.getCooldownManager().trigger(CooldownType.REQUEST, player.getUniqueId())) {
            TextUtil.sendMessage(player, plugin.getLineMessage("Request-Module.Request-Cooldown-Message")
                    .replace("%amount%", String.valueOf(plugin.getCooldownManager().getRemaining(CooldownType.REQUEST, player.getUniqueId(), TimeUnit.SECONDS))));
            return;
        }

        TextUtil.sendMessage(player, "Request-Module.Request-Sent");

        for (ProxiedPlayer loopPlayer : plugin.getProxy().getPlayers()) {

            if (!plugin.hasCustomPermission("Request-Notify"))
                continue;

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null || !loopUser.isStaffMessages())
                continue;

            String format = plugin.getListMessage("Request-Module.Request-Broadcast");

            TextUtil.sendMessage(loopPlayer, format
                    .replace("%player_server%", player.getServer().getInfo().getName())
                    .replace("%player%", player.getName())
                    .replace("%reason%", message.toString()));
        }
    }
}
