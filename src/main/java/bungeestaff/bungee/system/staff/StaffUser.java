package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.Serializable;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.util.ParseUtil;
import bungeestaff.bungee.util.TextUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StaffUser implements Serializable {

    @Getter
    private final UUID uniqueID;
    @Getter
    @Setter
    private String name;

    @Getter
    private final Rank rank;

    @Getter
    @Setter
    private boolean staffChat; // is typing into staff chat
    @Getter
    @Setter
    private boolean staffMessages; // wants to see staff chat

    @Getter
    @Setter
    private boolean online = false;
    @Getter
    @Setter
    // user is synced from another proxy, don't attempt to send messages to him
    private boolean remote = false;
    @Getter
    @Setter
    private String server;

    public StaffUser(UUID uniqueID, Rank rank) {
        this.uniqueID = uniqueID;
        this.rank = rank;
    }

    public boolean switchStaffChat() {
        return this.staffChat = !this.staffChat;
    }

    public boolean switchStaffMessages() {
        return this.staffMessages = !this.staffMessages;
    }

    public void sendStaffMessage(String message) {
        if (staffMessages)
            sendMessage(message);
    }

    public void sendMessage(String message) {
        if (!remote && online)
            TextUtil.sendMessage(asPlayer(), message);
    }

    @Nullable
    public ProxiedPlayer asPlayer() {
        return ProxyServer.getInstance().getPlayer(uniqueID);
    }

    @NotNull
    @Override
    public String serialize() {
        return uniqueID.toString() + ";" +
                name + ";" +
                rank.getName() + ";" +
                server + ";" +
                online;
    }

    // uniqueID;name;rank;server;online
    // Deserialized users are set remote to true
    @Nullable
    public static StaffUser deserialize(BungeeStaffPlugin plugin, String input) {
        String[] arr = input.split(";");

        if (arr.length < 4)
            return null;

        UUID uniqueID = ParseUtil.parseUUID(arr[0]);
        String name = arr[1];
        String rankName = arr[2];

        Rank rank = plugin.getRankManager().getRank(rankName);

        if (rank == null) {
            rank = plugin.getRankManager().getRank("default");
            plugin.getProxy().getLogger().warning("Remote user " + name + " has rank " + rankName + ", but it's not present in local configuration, using default.");
        }

        StaffUser user = new StaffUser(uniqueID, rank);
        if (!name.equalsIgnoreCase("null"))
            user.setName(name);
        user.setRemote(true);

        if (!arr[3].equalsIgnoreCase("null"))
            user.setServer(arr[3]);

        if (arr.length > 4) {
            boolean online = Boolean.parseBoolean(arr[4]);
            user.setOnline(online);
        }

        return user;
    }

    private void copyUseful(String server, String name) {
        if (this.server == null && server != null)
            this.server = server;
        if (this.name == null && name != null)
            this.name = name;
    }

    public void copyUseful(CachedUser user) {
        copyUseful(user.getServer(), user.getName());
    }

    public void copyUseful(StaffUser user) {
        copyUseful(user.getServer(), user.getName());
        if (!online)
            this.online = user.isOnline();
    }
}
