package bungeestaff.bungee.configuration;

import bungeestaff.bungee.BungeeStaffPlugin;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {

    private final BungeeStaffPlugin plugin;

    // Name with extension
    @Getter
    private final String name;

    @Getter
    private final File file;
    @Getter
    private final ConfigurationProvider configurationProvider;

    @Getter
    private Configuration configuration;

    public Config(BungeeStaffPlugin plugin, String name) {
        this.plugin = plugin;

        String finalName = name.contains(".yml") ? name : name.concat(".yml");
        this.name = finalName;
        this.file = new File(plugin.getDataFolder() + "/" + finalName);

        this.configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    }

    public void load() {
        if (!file.exists())
            try {
                InputStream in = plugin.getResourceAsStream(name);
                Files.copy(in, file.toPath());

                configuration = configurationProvider.load(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void delete() {
        Path path = Paths.get(plugin.getDataFolder().getPath(), name);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        delete();
        load();
    }

    public void save() {
        try {
            configurationProvider.save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
