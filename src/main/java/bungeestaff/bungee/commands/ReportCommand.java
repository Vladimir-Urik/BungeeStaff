package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.cooldown.CooldownType;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends CommandBase {

    public ReportCommand(BungeeStaffPlugin plugin) {
        super(plugin, "report");
        setPlayerOnly(true);
        setRange(2, -1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String[] args) {

        ProxiedPlayer player = (ProxiedPlayer) sender;

        StringBuilder reason = new StringBuilder();
        Arrays.stream(args).skip(1).forEach(str -> reason.append(" ").append(str));

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        CachedUser user;

        if (target == null) {
            // Try to fetch cached user from Rabbit
            user = plugin.getMessagingService().getUserCache().getUser(args[0]);

            if (user == null) {
                plugin.sendMessage(sender, "Report-Module.Player-Not-Found");
                return;
            }
        } else
            user = new CachedUser(target);

        // Can only be himself when on the same server, so we don't care about target being null.
        if (player.equals(target)) {
            plugin.sendMessage(sender, "Report-Module.Player-Sender");
            return;
        }

        if (!plugin.getCooldownManager().trigger(CooldownType.REPORT, player.getUniqueId())) {
            plugin.sendMessage(player, plugin.getMessage("Report-Module.Report-Cooldown-Message")
                    .replace("%amount%", String.valueOf(plugin.getCooldownManager().getRemaining(CooldownType.REPORT, player.getUniqueId(), TimeUnit.SECONDS))));
            return;
        }

        plugin.sendMessage(sender, "Report-Module.Report-Sent");

        String format = plugin.getMessage("Report-Module.Report-Broadcast");

        TextComponent message = TextUtil.format(format
                .replace("%reporter_server%", player.getServer().getInfo().getName())
                .replace("%reporter%", player.getName())
                .replace("%reported%", user.getName())
                .replace("%reported_server%", user.getServer())
                .replace("%reason%", reason));

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getMessage("Report-Module.Hover-Message")
                .replace("%reported%", user.getName())
                .replace("%reported_server%", user.getServer()))
                .create()));

        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, plugin.getMessage("Report-Module.JSONClick-Command")
                .replace("%reported%", user.getName())));

        if (plugin.getMessages().getBoolean("Report-Module.Report-Clickable", false)) {
            sendJson(message);

            // Rabbit message
            plugin.getMessagingService().sendMessage(MessageType.STAFF, format
                    .replace("%reporter_server%", player.getServer().getInfo().getName())
                    .replace("%reporter%", player.getName())
                    .replace("%reported%", user.getName())
                    .replace("%reported_server%", user.getServer())
                    .replace("%reason%", reason));
        } else
            plugin.getStaffManager().sendMessage(format
                    .replace("%reporter_server%", player.getServer().getInfo().getName())
                    .replace("%reporter%", player.getName())
                    .replace("%reported%", user.getName())
                    .replace("%reported_server%", user.getServer())
                    .replace("%reason%", reason), MessageType.STAFF);
    }

    private void sendJson(TextComponent component) {
        plugin.getStaffManager().getUsers(u -> u.asPlayer() != null && u.isOnline() && u.isStaffMessages())
                .forEach(u -> u.asPlayer().sendMessage(component));
    }
}
