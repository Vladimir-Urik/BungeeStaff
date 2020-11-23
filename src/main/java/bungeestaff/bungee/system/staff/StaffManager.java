package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.Config;
import bungeestaff.bungee.ParseUtil;
import bungeestaff.bungee.system.rank.Rank;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        Configuration config = storage.getConfiguration();

        for (String key : config.getKeys()) {
            UUID uniqueID = ParseUtil.parseUUID(key);

            if (uniqueID == null)
                continue;

            String name = config.getString(key + ".name");
            String rankName = config.getString(key + ".rank");

            Rank rank = plugin.getRankManager().getRank(rankName);

            StaffUser user = new StaffUser(uniqueID, rank);

            user.setName(name);
            user.setStaffChat(config.getBoolean(key + ".staff-chat", false));

            this.users.put(uniqueID, user);
        }
        plugin.getLogger().info("Loaded " + this.users.size() + " staff user(s)...");
    }

    public void save() {
        storage.clear();
        Configuration config = storage.getConfiguration();

        for (StaffUser user : this.users.values()) {
            config.set(user.getUniqueID().toString() + ".name", user.getName());
            config.set(user.getUniqueID().toString() + ".rank", user.getRank().getName());
            config.set(user.getUniqueID().toString() + ".staff-chat", user.isStaffChat());
        }

        storage.save();
    }

    public boolean hasChatEnabled(UUID uniqueID) {
        StaffUser user = getUser(uniqueID);
        return user != null && user.isStaffChat();
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
}
