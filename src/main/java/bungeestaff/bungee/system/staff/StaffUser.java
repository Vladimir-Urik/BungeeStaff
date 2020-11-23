package bungeestaff.bungee.system.staff;

import bungeestaff.bungee.system.rank.Rank;
import lombok.Getter;
import lombok.Setter;

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
    private boolean staffChat;
    @Getter
    @Setter
    private boolean online;

    public StaffUser(UUID uniqueID, Rank rank) {
        this.uniqueID = uniqueID;
        this.rank = rank;
    }
}
