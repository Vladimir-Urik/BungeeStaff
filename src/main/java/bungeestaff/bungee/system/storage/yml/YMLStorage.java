package bungeestaff.bungee.system.storage.yml;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.system.storage.IStaffStorage;
import bungeestaff.bungee.util.ParseUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class YMLStorage implements IStaffStorage {

    private final BungeeStaffPlugin plugin;

    @Getter
    private Config storage;

    @Getter
    private final String fileName;

    // L1 cache to mimic save&load, it would eat a lot of resources to save each time instead.
    private final Map<UUID, StaffUser> loadedUsers = new HashMap<>();

    public YMLStorage(BungeeStaffPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    @Override
    public boolean finish() {
        storage.clear();
        Configuration config = storage.getConfiguration();

        for (StaffUser user : loadedUsers.values()) {
            String uuidString = user.getUniqueID().toString();
            config.set(uuidString + ".name", user.getName());
            config.set(uuidString + ".rank", user.getRank().getName());
            config.set(uuidString + ".staff-chat", user.isStaffChat());
            config.set(uuidString + ".staff-messages", user.isStaffMessages());
        }

        return storage.save();
    }

    @Override
    public CompletableFuture<Void> saveAll(Collection<StaffUser> users) {
        return CompletableFuture.supplyAsync(() -> {
            users.forEach(u -> loadedUsers.put(u.getUniqueID(), u));
            finish();
            return null;
        });
    }

    @Override
    public boolean initialize() {
        this.storage = new Config(plugin, fileName);

        if (!storage.load())
            return false;

        loadedUsers.clear();

        Configuration section = storage.getConfiguration();

        for (String key : section.getKeys()) {
            UUID uniqueID = ParseUtil.parseUUID(key);

            if (uniqueID == null)
                continue;

            String name = section.getString(key + ".name");
            String rankName = section.getString(key + ".rank");

            Rank rank = plugin.getRankManager().getRank(rankName);

            if (rank == null) {
                rank = plugin.getRankManager().getRank("default");
                ProxyServer.getInstance().getLogger().warning("Rank " + rankName + " of " + name + " does no longer exist. Using the default rank.");
            }

            ProxiedPlayer player = plugin.getProxy().getPlayer(uniqueID);

            StaffUser user = new StaffUser(uniqueID, rank);

            if (player != null && Strings.isNullOrEmpty(name))
                name = player.getName();

            user.setName(name);
            user.setStaffChat(section.getBoolean(key + ".staff-chat", false));
            user.setStaffMessages(section.getBoolean(key + ".staff-messages", plugin.getConfig().getBoolean("Defaults.Staff-Messages", false)));

            loadedUsers.put(user.getUniqueID(), user);
        }

        plugin.getLogger().info("Loaded " + loadedUsers.size() + " staff user(s)...");
        return true;
    }

    @Override
    public CompletableFuture<Boolean> save(StaffUser user) {
        return CompletableFuture.supplyAsync(() -> {
            this.loadedUsers.put(user.getUniqueID(), user);
            return true;
        });
    }

    @Override
    public CompletableFuture<StaffUser> load(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> loadedUsers.get(uniqueID));
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            loadedUsers.remove(uniqueID);
            return true;
        });
    }

    @Override
    public CompletableFuture<Set<StaffUser>> loadAll() {
        return CompletableFuture.supplyAsync(() -> new HashSet<>(loadedUsers.values()));
    }
}
