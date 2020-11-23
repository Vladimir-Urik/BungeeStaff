package bungeestaff.bungee.system.rank;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Rank {

    @Getter
    private final String name;

    @Getter
    @Setter
    private String prefix;

    private final Set<UUID> users = new HashSet<>();

    public Rank(String name) {
        this.name = name;
    }

    public void add(UUID uniqueID) {
        this.users.add(uniqueID);
    }

    public void remove(UUID uniqueID) {
        this.users.remove(uniqueID);
    }

    public Set<UUID> getUsers() {
        return Collections.unmodifiableSet(this.users);
    }

    public boolean hasRank(UUID uniqueID) {
        return this.users.contains(uniqueID);
    }
}
