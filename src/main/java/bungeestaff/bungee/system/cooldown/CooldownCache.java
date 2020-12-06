package bungeestaff.bungee.system.cooldown;

import bungeestaff.bungee.BungeeStaffPlugin;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownCache {

    private final BungeeStaffPlugin plugin;

    private final Map<UUID, Long> cache = new HashMap<>();

    @Getter
    @Setter
    private long cooldown = 60000; // ms

    public CooldownCache(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean trigger(UUID uniqueID) {
        if (has(uniqueID))
            return false;

        start(uniqueID);
        return true;
    }

    public void start(UUID uniqueID) {
        this.cache.put(uniqueID, System.currentTimeMillis() + cooldown);

        plugin.getProxy().getScheduler().schedule(plugin, () -> remove(uniqueID), cooldown, TimeUnit.SECONDS);
    }

    public boolean has(UUID uniqueID) {
        if (this.cache.containsKey(uniqueID)) {
            if (this.cache.get(uniqueID) > System.currentTimeMillis())
                return true;
            else remove(uniqueID);
        }
        return false;
    }

    public void remove(UUID uniqueID) {
        this.cache.remove(uniqueID);
    }

    public long getRemaining(UUID uniqueID) {
        return this.cache.containsKey(uniqueID) ? this.cache.get(uniqueID) : 0;
    }
}
