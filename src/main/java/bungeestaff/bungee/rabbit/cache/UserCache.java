package bungeestaff.bungee.rabbit.cache;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.system.staff.StaffUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class UserCache {

    private final BungeeStaffPlugin plugin;

    private final String proxyId;

    private final Map<String, Set<CachedUser>> cachedUsers = new HashMap<>();

    public UserCache(BungeeStaffPlugin plugin, String proxyId) {
        this.plugin = plugin;
        this.proxyId = proxyId;
    }

    public CachedUser getUser(String name) {
        return getUsers().stream()
                .filter(u -> u.getName().equals(name))
                .findAny().orElse(null);
    }

    // Get all cached users
    public Set<CachedUser> getUsers() {
        Set<CachedUser> total = new HashSet<>();
        this.cachedUsers.keySet().forEach(c -> total.addAll(getUsers(c)));
        return total;
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

    // Add user from local proxy
    public void addUser(ProxiedPlayer player) {
        getUsers(proxyId).add(new CachedUser(player));
    }

    public void removeUser(String user) {
        this.cachedUsers.values().forEach(list -> list.removeIf(p -> p.getName().equals(user)));
    }

    public void updateUsers(String serverId, Collection<CachedUser> users) {
        this.cachedUsers.put(serverId, new HashSet<>(users));

        // Update staff users
        for (CachedUser user : users) {
            StaffUser staffUser = plugin.getStaffManager().getUser(user.getUniqueId());
            if (staffUser != null)
                staffUser.copyUseful(user);
        }
    }
}
