package bungeestaff.bungee.system.rank;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Config;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class RankManager {

    private final BungeeStaffPlugin plugin;

    private final Map<String, Rank> ranks = new HashMap<>();

    @Getter
    private final Config config;

    public RankManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;

        this.config = new Config(plugin, "ranks");
    }

    public void load() {
        this.config.load();

        Configuration ranks = plugin.getConfig().getSection("Ranks");

        if (ranks == null)
            return;

        for (String key : ranks.getKeys()) {
            String prefix = ranks.getString(key + ".prefix");
            Rank rank = new Rank(key);
            rank.setPrefix(prefix);
            this.ranks.put(key, rank);
        }

        plugin.getLogger().info("Loaded " + this.ranks.size() + " rank(s)...");
    }

    public Rank getRank(String name) {
        return this.ranks.get(name);
    }
}
