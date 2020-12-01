package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.configuration.Config;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.util.ParseUtil;
import bungeestaff.bungee.util.TextUtil;
import com.google.common.base.Strings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
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

            if (rank == null) {
                rank = plugin.getRankManager().getRank("default");
                ProxyServer.getInstance().getLogger().warning("Rank " + rankName + " of " + name + " does no longer exist. Using the default rank.");
            }

            ProxiedPlayer player = plugin.getProxy().getPlayer(uniqueID);

            StaffUser user = new StaffUser(uniqueID, rank);

            if (player != null) {
                if (Strings.isNullOrEmpty(name))
                    name = player.getName();

                if (player.isConnected()) {
                    user.setOnline(true);

                    if (player.getServer() != null)
                        user.setServer(player.getServer().getInfo().getName());
                }
            }

            user.setName(name);
            user.setStaffChat(section.getBoolean(key + ".staff-chat", false));
            user.setStaffMessages(section.getBoolean(key + ".staff-messages", plugin.getConfig().getBoolean("Defaults.Staff-Messages", false)));

            addUser(user, false);
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
    public StaffUser getUser(ProxiedPlayer player) {
        if (player == null)
            return null;
        return getUser(player.getUniqueId());
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

    public void addUser(StaffUser user, boolean sync) {
        this.users.put(user.getUniqueID(), user);
        user.setRemote(false);

        if (sync)
            plugin.getMessagingService().sendStaffAdd(user);
    }

    public void addUser(CachedUser cachedUser, Rank rank, boolean sync) {
        StaffUser user = new StaffUser(cachedUser.getUniqueId(), rank);

        user.setName(cachedUser.getName());
        user.setServer(cachedUser.getServer());
        user.setOnline(true);
        user.setStaffMessages(plugin.getConfig().getBoolean("Defaults.Staff-Messages", false));

        addUser(user, sync);
    }

    public void removeUser(StaffUser user, boolean sync) {
        this.users.remove(user.getUniqueID());

        if (sync)
            plugin.getMessagingService().sendStaffRemove(user.getName());
    }

    public Set<StaffUser> getUsers() {
        return new HashSet<>(this.users.values());
    }

    public Set<StaffUser> getUsers(Predicate<StaffUser> condition) {
        return this.users.values().stream()
                .filter(condition)
                .collect(Collectors.toSet());
    }

    public void importUser(StaffUser user) {
        // Don't override from remote, just add if missing
        if (this.users.containsKey(user.getUniqueID())) {

            // Update server if possible
            StaffUser localUser = getUser(user.getUniqueID());
            if (localUser != null)
                localUser.copyUseful(user);
            return;
        }

        addUser(user, false);
    }

    public void importUsers(Set<StaffUser> users) {
        users.forEach(this::importUser);
    }

    /**
     * Send message to online staff/players and sync over rabbit.
     */
    public void sendMessage(String message, @NotNull MessageType type) {

        // Send one to console
        TextUtil.sendMessage(plugin.getProxy().getConsole(), message);

        if (type == MessageType.STAFF)
            // To staff online
            getUsers().forEach(u -> u.sendStaffMessage(message));
        else if (type == MessageType.PUBLIC)
            // To all players online
            plugin.getProxy().getPlayers().forEach(p -> TextUtil.sendMessage(p, message));

        plugin.getMessagingService().sendMessage(type, message);
    }

    /**
     * Send message to online staff.
     */
    public void sendMessage(String message) {
        // Send one to console
        TextUtil.sendMessage(plugin.getProxy().getConsole(), message);

        getUsers().forEach(u -> u.sendStaffMessage(message));
    }

    /**
     * Format and send a message to staff chat.
     */
    public void sendStaffMessage(StaffUser author, String message) {
        String wholeMessage = plugin.getMessages().getString("StaffChat-Module.StaffChat-Message")
                .replace("%server%", TextUtil.nullOr(author::getServer, "none"))
                .replace("%player%", author.getName())
                .replace("%message%", message)
                .replace("%prefix%", plugin.getPrefix(author));

        sendMessage(wholeMessage, MessageType.STAFF);
    }
}
