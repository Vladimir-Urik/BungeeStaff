package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.TextUtil;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.cooldown.CooldownType;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

public class ReportCommand extends CommandBase {

    public ReportCommand(BungeeStaffPlugin plugin) {
        super(plugin, "report");
        setPlayerOnly(true);
        setRange(2, -1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;

        StringBuilder reason = new StringBuilder();
        Arrays.stream(args).skip(1).forEach(reason::append);

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            plugin.sendLineMessage("Report-Module.Player-Not-Found", sender);
            return;
        }

        if (player.equals(target)) {
            plugin.sendLineMessage("Report-Module.Player-Sender", sender);
            return;
        }

        if (!plugin.getCooldownManager().triggerCooldown(CooldownType.REPORT, player.getUniqueId())) {
            plugin.sendMessage(plugin.getLineMessage("Report-Module.Report-Cooldown-Message")
                    .replace("%amount%", String.valueOf(plugin.getCooldownManager().getRemaining(CooldownType.REPORT, player.getUniqueId()))), player);
            return;
        }

        plugin.sendLineMessage("Report-Module.Report-Sent", sender);

        for (ProxiedPlayer loopPlayer : plugin.getProxy().getPlayers()) {
            if (!plugin.hasCustomPermission("Report-Notify", loopPlayer))
                continue;

            StaffUser loopUser = plugin.getStaffManager().getUser(loopPlayer.getUniqueId());

            if (loopUser == null || !loopUser.isOnline() || !loopUser.isStaffChat())
                continue;

            String broadcast = plugin.getListMessage("Report-Module.Report-Broadcast");

            if (plugin.getMessages().getBoolean("Report-Module.Report-Clickable")) {
                BaseComponent[] converted = TextComponent.fromLegacyText(broadcast
                        .replace("%reporter_server%", player.getServer().getInfo().getName())
                        .replace("%reporter%", player.getName())
                        .replace("%reported%", target.getName())
                        .replace("%reported_server%", target.getServer().getInfo().getName())
                        .replace("%reason%", reason));

                TextComponent message = new TextComponent(converted);
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getLineMessage("Report-Module.Hover-Message")
                        .replace("%reported%", target.getName())
                        .replace("%reported_server%", target.getServer().getInfo().getName()))
                        .create()));

                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getLineMessage("Report-Module.JSONClick-Command")
                        .replace("%reported%", target.getName())));

                loopPlayer.sendMessage(message);
            } else {
                TextUtil.sendMessage(loopPlayer, broadcast
                        .replace("%reporter_server%", player.getServer().getInfo().getName())
                        .replace("%reporter%", player.getName())
                        .replace("%reported%", target.getName())
                        .replace("%reported_server%", target.getServer().getInfo().getName())
                        .replace("%reason%", reason));
            }
        }
    }
}
