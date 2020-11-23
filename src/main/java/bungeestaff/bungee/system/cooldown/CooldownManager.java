package bungeestaff.bungee.system.cooldown;

import bungeestaff.bungee.BungeeStaffPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final BungeeStaffPlugin plugin;

    private final Map<CooldownType, CooldownCache> cooldownCaches = new HashMap<>();

    public CooldownManager(BungeeStaffPlugin plugin) {
        this.plugin = plugin;

        cooldownCaches.put(CooldownType.REPORT, new CooldownCache(plugin));
        cooldownCaches.put(CooldownType.REQUEST, new CooldownCache(plugin));
    }

    public void load() {
        //TODO load cooldowns
    }

    public boolean triggerCooldown(CooldownType type, UUID uniqueID) {
        return this.cooldownCaches.get(type).triggerCooldown(uniqueID);
    }

    public boolean hasCooldown(CooldownType type, UUID uniqueID) {
        return this.cooldownCaches.get(type).hasCooldown(uniqueID);
    }
}
