package bungeestaff.bungee.system.rank;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rank rank = (Rank) o;
        return name.equals(rank.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
