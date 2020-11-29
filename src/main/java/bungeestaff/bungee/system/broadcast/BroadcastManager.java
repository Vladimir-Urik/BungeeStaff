package bungeestaff.bungee.system.broadcast;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class BroadcastManager {

    private final BungeeStaffPlugin plugin;

    private final Map<String, BroadcastFormat> formats = new HashMap<>();

    private final Config config;

    public BroadcastManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
        this.config = new Config(plugin, "broadcasts");
    }

    public void load() {
        config.load();
        formats.clear();

        Configuration section = config.getConfiguration();

        for (String name : section.getKeys()) {
            BroadcastFormat format = new BroadcastFormat(name);
            format.setLines(section.getStringList(name + ".format"));
            formats.put(name, format);
        }
        plugin.getLogger().info("Loaded " + formats.size() + " broadcast format(s)...");
    }

    public BroadcastFormat getFormat(String name) {
        return this.formats.get(name);
    }

    public void broadcastRaw(String message, boolean send) {
        plugin.getProxy().getPlayers().forEach(player -> TextUtil.sendMessage(player, message));

        if (send)
            plugin.getMessagingManager().sendMessage(MessageType.PUBLIC, message);
    }

    public void broadcast(BroadcastFormat format, PlaceholderContainer placeholders) {
        String message = placeholders.parse(String.join("\n&r", format.getLines()));

        broadcastRaw(message, true);
    }
}
