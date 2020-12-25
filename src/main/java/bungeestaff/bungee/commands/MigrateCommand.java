package bungeestaff.bungee.commands;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.commands.framework.CommandBase;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.system.storage.impl.ConnectionInfo;
import bungeestaff.bungee.system.storage.impl.MySQLStorage;
import bungeestaff.bungee.system.storage.impl.ServerConnection;
import bungeestaff.bungee.system.storage.yml.YMLStorage;
import bungeestaff.bungee.util.TextUtil;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.Nullable;

public class MigrateCommand extends CommandBase {

    public MigrateCommand(BungeeStaffPlugin plugin) {
        super(plugin, "bstaffmigrate", "Migrate-Database");
        setRange(1);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Migration migration = Migration.fromString(args[0]);

        if (migration == null) {
            TextUtil.sendMessage(sender, "&cInvalid migration.");
            return;
        }

        TextUtil.sendMessage(sender, String.format("&7&oMigrating to &f&o%s", migration.toString()));
        migration.migrate(plugin, sender);
    }

    enum Migration {
        YAML((plugin, requester) -> {

            MySQLStorage mySQLStorage = initializeMySql(plugin, requester);

            if (mySQLStorage == null) {
                return;
            }

            // Initialize yml.

            YMLStorage ymlStorage = initializeYml(plugin, requester);

            if (ymlStorage == null) {
                return;
            }

            // Load

            mySQLStorage.loadAll().thenAcceptAsync(users -> {
                for (StaffUser user : users) {
                    ymlStorage.save(user);
                }

                // Close
                mySQLStorage.finish();
                ymlStorage.finish();

                TextUtil.sendMessage(requester, String.format("&7Migrated &f%d &7staff users.", users.size()));
            });
        }),

        MYSQL((plugin, requester) -> {
            MySQLStorage mySQLStorage = initializeMySql(plugin, requester);

            if (mySQLStorage == null) {
                return;
            }

            // Initialize yml.

            YMLStorage ymlStorage = initializeYml(plugin, requester);

            if (ymlStorage == null) {
                return;
            }

            // Load
            ymlStorage.loadAll().thenAcceptAsync(users -> {
                for (StaffUser user : users) {
                    mySQLStorage.save(user);
                }

                // Close
                mySQLStorage.finish();
                ymlStorage.finish();

                TextUtil.sendMessage(requester, String.format("&7Migrated &f%d &7staff users.", users.size()));
            });
        });

        private final MigrationHandler handler;

        Migration(MigrationHandler handler) {
            this.handler = handler;
        }

        @Nullable
        public static MigrateCommand.Migration fromString(String str) {
            try {
                return valueOf(str.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public void migrate(BungeeStaffPlugin plugin, CommandSender sender) {
            handler.migrate(plugin, sender);
        }

        private static MySQLStorage initializeMySql(BungeeStaffPlugin plugin, CommandSender sender) {
            ConnectionInfo connectionInfo = ConnectionInfo.load(plugin.getConfig().getSection("storage.mysql"));

            if (connectionInfo == null) {
                TextUtil.sendMessage(sender, "&cFailed to connect to mysql.");
                return null;
            }

            ServerConnection connection = new ServerConnection(connectionInfo);

            MySQLStorage mySQLStorage = new MySQLStorage(plugin, connection, plugin.getConfig().getString("storage.mysql.tables.users"));

            if (!mySQLStorage.initialize()) {
                TextUtil.sendMessage(sender, "&cFailed to initialize MySQL storage.");
                return null;
            }
            return mySQLStorage;
        }

        private static YMLStorage initializeYml(BungeeStaffPlugin plugin, CommandSender sender) {
            YMLStorage ymlStorage = new YMLStorage(plugin, plugin.getConfig().getString("storage.yml.file", "users"));

            if (!ymlStorage.initialize()) {
                TextUtil.sendMessage(sender, "&cFailed to initialize yml storage.");
                return null;
            }

            return ymlStorage;
        }
    }

    private interface MigrationHandler {
        void migrate(BungeeStaffPlugin plugin, CommandSender requester);
    }
}
