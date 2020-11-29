package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.system.cooldown.CooldownType;
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

        StringBuilder message = new StringBuilder();
        Arrays.stream(args).skip(1).forEach(str -> message.append(" ").append(str));

        if (!plugin.getCooldownManager().trigger(CooldownType.REQUEST, player.getUniqueId())) {
            TextUtil.sendMessage(player, plugin.getLineMessage("Request-Module.Request-Cooldown-Message")
                    .replace("%amount%", String.valueOf(plugin.getCooldownManager().getRemaining(CooldownType.REQUEST, player.getUniqueId(), TimeUnit.SECONDS))));
            return;
        }

        TextUtil.sendMessage(player, "Request-Module.Request-Sent");

        String format = plugin.getListMessage("Request-Module.Request-Broadcast");

        plugin.getStaffManager().sendRawMessage(format
                .replace("%player_server%", player.getServer().getInfo().getName())
                .replace("%player%", player.getName())
                .replace("%reason%", message.toString()), MessageType.STAFF);
    }
}
