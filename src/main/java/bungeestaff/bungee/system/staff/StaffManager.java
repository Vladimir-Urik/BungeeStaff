package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffManager {

    private final BungeeStaffPlugin plugin;

    private final Map<UUID, StaffUser> staff = new HashMap<>();

    public StaffManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        //TODO
    }

    public boolean hasChatEnabled(UUID uniqueID) {
        StaffUser user = getUser(uniqueID);
        return user != null && user.isStaffChat();
    }

    @Nullable
    public StaffUser getUser(UUID uniqueID) {
        return this.staff.get(uniqueID);
    }

    @Nullable
    public StaffUser getUser(String name) {
        return this.staff.values().stream()
                .filter(u -> u.getName().equals(name))
                .findAny().orElse(null);
    }
}
