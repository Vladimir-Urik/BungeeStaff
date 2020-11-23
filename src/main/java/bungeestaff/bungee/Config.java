package bungeestaff.bungee;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    private final BungeeStaffPlugin plugin;

    @Getter
    private final File file;
    @Getter
    private final ConfigurationProvider configurationProvider;

    @Getter
    private Configuration configuration;

    public Config(BungeeStaffPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);

        load();
    }

    public void load() {
        if (!file.exists())
            try {
                InputStream in = plugin.getResourceAsStream("config.yml");
                Files.copy(in, file.toPath());

                configurationProvider.load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static Config obtain(BungeeStaffPlugin plugin, String path) {
        File file = new File(path);
        return new Config(plugin, file);
    }
}
