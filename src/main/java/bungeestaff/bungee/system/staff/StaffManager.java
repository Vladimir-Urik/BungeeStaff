package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.MessageType;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.storage.IStaffStorage;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StaffManager {

    private final BungeeStaffPlugin plugin;

    private final Map<UUID, StaffUser> users = new HashMap<>();

    private final IStaffStorage storage;

    public StaffManager(BungeeStaffPlugin plugin, IStaffStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
    }

    public void load() {
        if (!storage.initialize()) {
            plugin.getLogger().warning("Could not initialize staff user storage.");
            return;
        }

        storage.loadAll();
    }

    public void save() {
        if (!storage.finish())
            plugin.getLogger().warning("Could not close/save staff user storage.");
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

    // Add user to staff
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
                .replace("%server%", TextUtil.getOr(author::getServer, "none"))
                .replace("%player%", author.getName())
                .replace("%message%", message)
                .replace("%prefix%", plugin.getPrefix(author));

        sendMessage(wholeMessage, MessageType.STAFF);
    }
}
