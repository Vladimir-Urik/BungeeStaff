package bungeestaff.bungee.rabbit.cache;

import bungeestaff.bungee.system.Serializable;
import bungeestaff.bungee.util.ParseUtil;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CachedUser implements Serializable {

    @Getter
    private final UUID uniqueId;
    @Getter
    private final String name;
    @Getter
    private final String server;

    public CachedUser(ProxiedPlayer player) {
        this.name = player.getName();
        this.uniqueId = player.getUniqueId();
        this.server = ParseUtil.getOr(() -> player.getServer().getInfo().getName(), null);
    }

    public CachedUser(UUID uniqueId, String name, String server) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.server = server;
    }

    @NotNull
    public static String serializeFrom(ProxiedPlayer player) {
        return new CachedUser(player).serialize();
    }

    @Override
    @NotNull
    public String serialize() {
        return uniqueId + ";" +
                name + ";" +
                server;
    }

    @Nullable
    public static CachedUser deserialize(String input) {
        String[] arr = input.split(";");

        if (arr.length < 3)
            return null;

        UUID uniqueId = ParseUtil.parseUUID(arr[0]);

        if (uniqueId == null)
            return null;

        return new CachedUser(uniqueId, arr[1], arr[2]);
    }
}
