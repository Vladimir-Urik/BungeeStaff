package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.BungeeStaffPlugin;
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

import java.util.Optional;
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
        asPlayer().ifPresent(p -> TextUtil.sendMessage(p, message));
    }

    public boolean isOnline() {
        return BungeeStaffPlugin.getInstance().getUserCache().isOnline(name);
    }

    @Nullable
    public String getServer() {
        return BungeeStaffPlugin.getInstance().getUserCache().getServer(name);
    }

    public Optional<ProxiedPlayer> asPlayer() {
        return Optional.ofNullable(ProxyServer.getInstance().getPlayer(uniqueID));
    }

    @NotNull
    @Override
    public String serialize() {
        return uniqueID.toString() + ";" +
                name + ";" +
                rank.getName() + ";" +
                staffChat + ";" +
                staffMessages;
    }

    // uniqueID;name;rank;staffChat;staffMessages
    @Nullable
    public static StaffUser deserialize(BungeeStaffPlugin plugin, String input) {
        String[] arr = input.split(";");

        if (arr.length < 5)
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

        boolean staffChat = Boolean.parseBoolean(arr[3]);
        user.setStaffChat(staffChat);

        boolean staffMessages = Boolean.parseBoolean(arr[4]);
        user.setStaffMessages(staffMessages);

        return user;
    }
}
