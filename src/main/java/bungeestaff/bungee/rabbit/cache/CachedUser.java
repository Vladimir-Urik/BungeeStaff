package bungeestaff.bungee.rabbit.cache;

import bungeestaff.bungee.system.Serializable;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CachedUser implements Serializable {

    @Getter
    private final String name;
    @Getter
    private final String server;

    public CachedUser(ProxiedPlayer player) {
        this.name = player.getName();
        this.server = player.getServer().getInfo().getName();
    }

    public CachedUser(String name, String server) {
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
        return name + ";" +
                server;
    }

    @Nullable
    public static CachedUser deserialize(String input) {
        String[] arr = input.split(";");

        if (arr.length < 2)
            return null;

        return new CachedUser(arr[0], arr[1]);
    }
}
