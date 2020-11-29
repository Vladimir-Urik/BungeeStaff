package bungeestaff.bungee.system.broadcast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaceholderContainer {

    private final Map<String, String> placeholders = new HashMap<>();

    public PlaceholderContainer() {
    }

    public void add(String key, String value) {
        this.placeholders.put(key, value);
    }

    public List<String> parse(List<String> list) {
        return list.stream()
                .map(this::parse)
                .collect(Collectors.toList());
    }

    public String parse(String str) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();

            if (!key.startsWith("%") && !key.endsWith("%"))
                key = "%" + key + "%";

            str = str.replaceAll("(?i)" + key, entry.getValue());
        }
        return str;
    }

    @Override
    public String toString() {
        return this.placeholders.toString();
    }
}
