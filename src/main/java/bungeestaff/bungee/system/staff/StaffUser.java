package bungeestaff.bungee.system.staff;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class StaffUser {

    @Getter
    private final UUID uniqueID;
    @Getter
    private final String name;

    @Getter
    private final String rank;

    @Getter
    @Setter
    private boolean staffChat;
    @Getter
    @Setter
    private boolean online;

    public StaffUser(UUID uniqueID, String name, String rank) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.rank = rank;
    }
}
