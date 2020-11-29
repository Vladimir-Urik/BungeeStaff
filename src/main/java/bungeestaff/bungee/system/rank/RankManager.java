package bungeestaff.bungee.system.rank;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RankManager {

    private final BungeeStaffPlugin plugin;

    private final Map<String, Rank> ranks = new LinkedHashMap<>();

    @Getter
    private final Config config;

    public RankManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;

        this.config = new Config(plugin, "ranks");
    }

    public void load() {

        config.load();
        ranks.clear();

        Configuration section = config.getConfiguration();

        for (String key : section.getKeys()) {
            String prefix = section.getString(key + ".prefix");

            Rank rank = new Rank(key);
            rank.setPrefix(prefix);

            this.ranks.put(key, rank);
        }

        plugin.getLogger().info("Loaded " + this.ranks.size() + " rank(s)...");
    }

    public Rank getRank(String name) {
        return this.ranks.get(name);
    }

    public Set<Rank> getRanks() {
        return new HashSet<>(this.ranks.values());
    }
}
