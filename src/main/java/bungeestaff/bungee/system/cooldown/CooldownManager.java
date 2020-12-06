package bungeestaff.bungee.system.cooldown;

import bungeestaff.bungee.BungeeStaffPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private final BungeeStaffPlugin plugin;

    private final Map<CooldownType, CooldownCache> caches = new HashMap<>();

    public CooldownManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;

        caches.put(CooldownType.REPORT, new CooldownCache(plugin));
        caches.put(CooldownType.REQUEST, new CooldownCache(plugin));
    }

    public void load() {
        caches.get(CooldownType.REQUEST).setCooldown(plugin.getConfig().getLong("Cooldowns.Request", 60));
        caches.get(CooldownType.REPORT).setCooldown(plugin.getConfig().getLong("Cooldowns.Report", 60));
    }

    /**
     * Return remaining cooldown for player in milliseconds.
     */
    public long getRemaining(CooldownType type, UUID uniqueID) {
        return this.caches.get(type).getRemaining(uniqueID);
    }

    /**
     * Return remaining cooldown for player in chosen time unit.
     */
    public long getRemaining(CooldownType type, UUID uniqueID, TimeUnit unit) {
        return unit.convert(getRemaining(type, uniqueID), TimeUnit.MILLISECONDS);
    }

    public boolean trigger(CooldownType type, UUID uniqueID) {
        return this.caches.get(type).trigger(uniqueID);
    }

    public boolean hasCooldown(CooldownType type, UUID uniqueID) {
        return this.caches.get(type).has(uniqueID);
    }
}
