package bungeestaff.bungee.system;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.staff.StaffUser;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class UserCache {

    private final BungeeStaffPlugin plugin;

    private final String proxyId;

    private final Map<String, Set<CachedUser>> cachedUsers = new HashMap<>();

    @Getter
    @Setter
    private boolean messaging = false;

    public UserCache(BungeeStaffPlugin plugin, String proxyId) {
        this.plugin = plugin;
        this.proxyId = proxyId;
    }

    public boolean hasUserCached(String name) {
        return messaging && getUsers(proxyId).stream().anyMatch(u -> u.getName().equals(name));
    }

    public boolean isOnlineLocal(String name) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(name);
        return player != null && player.isConnected();
    }

    public boolean isOnline(String name) {
        return hasUserCached(name) || isOnlineLocal(name);
    }

    public Optional<String> getServerCached(String name) {
        return messaging ? getCachedUser(name).map(CachedUser::getServer) : Optional.empty();
    }

    @Nullable
    public String getServerLocal(String name) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(name);
        if (player == null)
            return null;
        return player.getServer().getInfo().getName();
    }

    @Nullable
    public String getServer(String name) {
        return getServerCached(name).orElse(getServerLocal(name));
    }

    @Nullable
    public CachedUser getUser(String name) {
        return getCachedUser(name).orElseGet(() -> createUser(name));
    }

    @Nullable
    public CachedUser createUser(String name) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(name);
        if (player == null)
            return null;
        return new CachedUser(player);
    }

    public Optional<CachedUser> getCachedUser(String name) {
        return getUsers().stream()
                .filter(u -> u.getName().equals(name))
                .findAny();
    }

    // Get all cached users
    public Set<CachedUser> getUsers() {
        Set<CachedUser> total = new HashSet<>();
        this.cachedUsers.keySet().forEach(c -> total.addAll(getUsers(c)));
        return total;
    }

    // Get online users on all proxies
    public Set<CachedUser> getOnlineUsers() {
        Set<CachedUser> users = getUsers();
        plugin.getProxy().getPlayers().forEach(p -> users.add(new CachedUser(p)));
        return users;
    }

    // Try to get cached users for serverId.
    // Create, cache and return a blank set if not present.
    public Set<CachedUser> getUsers(String serverId) {
        if (cachedUsers.containsKey(serverId))
            return cachedUsers.get(serverId);

        Set<CachedUser> users = new HashSet<>();
        cachedUsers.put(serverId, users);
        return users;
    }

    // Import users from a different proxy
    public void importUsers(String proxyId, Collection<CachedUser> users) {
        this.cachedUsers.put(proxyId, new HashSet<>(users));

        // Update staff users
        for (CachedUser user : users) {
            StaffUser staffUser = plugin.getStaffManager().getUser(user.getUniqueID());
            // Update name if needed
            if (staffUser != null && staffUser.getName() == null && user.getName() != null)
                staffUser.setName(user.getName());
        }
    }
}
