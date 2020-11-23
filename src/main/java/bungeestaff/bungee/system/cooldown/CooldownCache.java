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

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @Getter
    @Setter
    private long cooldown = 60000; // ms

    public CooldownCache(BungeeStaffPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean triggerCooldown(UUID uniqueID) {
        if (hasCooldown(uniqueID))
            return false;

        startCooldown(uniqueID);
        return true;
    }

    public void startCooldown(UUID uniqueID) {
        this.cooldowns.put(uniqueID, System.currentTimeMillis() + cooldown);

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            removeCooldown(uniqueID);
        }, cooldown, TimeUnit.MILLISECONDS);
    }

    public boolean hasCooldown(UUID uniqueID) {
        if (this.cooldowns.containsKey(uniqueID)) {
            if (this.cooldowns.get(uniqueID) > System.currentTimeMillis())
                return true;
            else removeCooldown(uniqueID);
        }
        return false;
    }

    public void removeCooldown(UUID uniqueID) {
        this.cooldowns.remove(uniqueID);
    }
}
