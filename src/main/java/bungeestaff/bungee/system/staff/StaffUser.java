package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.system.rank.Rank;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class StaffUser {

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
    private boolean online;

    @Getter
    @Setter
    private boolean staffMessages; // wants to see staff chat

    public StaffUser(UUID uniqueID, Rank rank) {
        this.uniqueID = uniqueID;
        this.rank = rank;
    }

    public boolean switchStaffChat() {
        return (this.staffChat = !this.staffChat);
    }

    public boolean switchStaffMessages() {
        return (this.staffMessages = !this.staffMessages);
    }

    public ProxiedPlayer asPlayer() {
        return ProxyServer.getInstance().getPlayer(uniqueID);
    }
}
