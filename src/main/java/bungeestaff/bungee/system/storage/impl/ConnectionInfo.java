package bungeestaff.bungee.system.storage.impl;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.Nullable;

public class ConnectionInfo {

    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final String username;
    @Getter
    private final String password;
    @Getter
    private final String database;
    @Getter
    @Setter
    private boolean readOnly = false;

    public ConnectionInfo(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Nullable
    public static ConnectionInfo load(Configuration section) {
        if (section == null)
            return null;

        return new ConnectionInfo(section.getString("host", "localhost"),
                section.getInt("port", 3306),
                section.getString("user", "root"),
                section.getString("pass", "secretpassword"),
                section.getString("database", "bungeestaff"));
    }

    @Override
    public String toString() {
        return username + "@" + host + ":" + port + "/" + database + " -p " + beepOut(password);
    }

    private String beepOut(String input) {
        StringBuilder str = new StringBuilder();
        for (int n = 0; n < input.length(); n++)
            str.append("*");
        return str.toString();
    }
}
