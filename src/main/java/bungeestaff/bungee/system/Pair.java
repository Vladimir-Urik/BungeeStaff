package bungeestaff.bungee.system;

import lombok.Data;

// Who would import apache commons to do this.
@Data
public class Pair<X, Y> {
    private final X key;
    private final Y value;
}
