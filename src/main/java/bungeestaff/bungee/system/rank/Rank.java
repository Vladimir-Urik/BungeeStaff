package bungeestaff.bungee.system.rank;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class Rank {

    @Getter
    private final String name;

    @Getter
    @Setter
    private String prefix;

    public Rank(String name) {
        this.name = name;
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
