package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.util.ParseUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StaffManager {

    private final BungeeStaffPlugin plugin;

    private final Map<UUID, StaffUser> users = new HashMap<>();

    private final Config storage;

    public StaffManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
        this.storage = new Config(plugin, "users");
    }

    public void load() {

        storage.load();
        users.clear();

        Configuration section = storage.getConfiguration();

        for (String key : section.getKeys()) {
            UUID uniqueID = ParseUtil.parseUUID(key);

            if (uniqueID == null)
                continue;

            String name = section.getString(key + ".name");
            String rankName = section.getString(key + ".rank");

            Rank rank = plugin.getRankManager().getRank(rankName);

            if (rank == null)
                ProxyServer.getInstance().getLogger().warning("Rank " + rankName + " of " + name + " does no longer exist.");

            StaffUser user = new StaffUser(uniqueID, rank);

            user.setName(name);
            user.setStaffChat(section.getBoolean(key + ".staff-chat", false));
            user.setStaffMessages(section.getBoolean(key + ".staff-messages", false));

            this.users.put(uniqueID, user);
        }
        plugin.getLogger().info("Loaded " + this.users.size() + " staff user(s)...");
    }

    public void save() {
        storage.clear();
        Configuration config = storage.getConfiguration();

        for (StaffUser user : this.users.values()) {
            String uuidString = user.getUniqueID().toString();
            config.set(uuidString + ".name", user.getName());
            config.set(uuidString + ".rank", user.getRank().getName());
            config.set(uuidString + ".staff-chat", user.isStaffChat());
            config.set(uuidString + ".staff-messages", user.isStaffMessages());
        }

        storage.save();
    }

    @Nullable
    public StaffUser getUser(UUID uniqueID) {
        return this.users.get(uniqueID);
    }

    @Nullable
    public StaffUser getUser(String name) {
        return this.users.values().stream()
                .filter(u -> u.getName().equals(name))
                .findAny().orElse(null);
    }

    public Set<StaffUser> getUsers() {
        return new HashSet<>(this.users.values());
    }

    public Set<StaffUser> getUsers(Predicate<StaffUser> condition) {
        return this.users.values().stream()
                .filter(condition)
                .collect(Collectors.toSet());
    }
}
