package bungeestaff.bungee.system.broadcast;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class BroadcastFormat {

    @Getter
    private final String name;

    private final List<String> lines = new LinkedList<>();

    public BroadcastFormat(String name) {
        this.name = name;
    }

    public void setLines(List<String> lines) {
        this.lines.clear();
        this.lines.addAll(lines);
    }

    public List<String> getLines() {
        return new LinkedList<>(lines);
    }
}
