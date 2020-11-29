package bungeestaff.bungee.system.broadcast;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.List;
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
            List<String> lines = section.getStringList(name + ".format");
            BroadcastFormat format = new BroadcastFormat(name);
            format.setLines(lines);
            formats.put(name, format);
        }
        plugin.getLogger().info("Loaded " + formats.size() + " broadcast format(s)...");
    }

    public BroadcastFormat getFormat(String name) {
        return this.formats.get(name);
    }

    public void broadcast(BroadcastFormat format, PlaceholderContainer placeholders) {
        String message = placeholders.parse(String.join("\n", format.getLines()));

        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            TextUtil.sendMessage(player, message);
        }
    }
}
