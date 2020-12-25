package bungeestaff.bungee.system.storage.impl;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.system.rank.Rank;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.system.storage.IStaffStorage;
import bungeestaff.bungee.util.ParseUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MySQLStorage implements IStaffStorage {

    private final BungeeStaffPlugin plugin;

    private final ServerConnection connection;

    @Getter
    private final String table;

    public MySQLStorage(BungeeStaffPlugin plugin, ServerConnection connection, String table) {
        this.plugin = plugin;
        this.connection = connection;
        this.table = table;
    }

    @Override
    public CompletableFuture<Void> saveAll(Collection<StaffUser> users) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (StaffUser user : users) {
            futures.add(save(user).thenAcceptAsync(res -> {
                if (!res)
                    plugin.getLogger().severe("Could not save user " + user.getName());
            }));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> save(StaffUser user) {
        return connection.execute(Query.SAVE_STAFF_USER.get(table),
                user.getUniqueID().toString(),
                user.getName(),
                user.getRank().getName(),
                user.isStaffChat(),
                user.isStaffMessages(),

                // UPDATE ON DUP
                user.getUniqueID().toString(),
                user.getName(),
                user.getRank().getName(),
                user.isStaffChat(),
                user.isStaffMessages());
    }

    @Override
    public CompletableFuture<StaffUser> load(UUID uniqueID) {
        CompletableFuture<StaffUser> future = new CompletableFuture<>();
        connection.executeQuery(Query.LOAD_STAFF_USER.get(table), uniqueID.toString()).thenAcceptAsync(set -> {
            try {
                if (set.next()) {
                    String name = set.getString("name");
                    String rankName = set.getString("rank");
                    boolean staffChat = set.getBoolean("staff_chat");
                    boolean staffMessages = set.getBoolean("staff_messages");

                    Rank rank = findRank(rankName);

                    StaffUser user = new StaffUser(uniqueID, rank);
                    user.setName(name);
                    user.setStaffChat(staffChat);
                    user.setStaffMessages(staffMessages);
                    future.complete(user);
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            future.complete(null);
        });
        return future;
    }

    @Override
    public CompletableFuture<Set<StaffUser>> loadAll() {
        CompletableFuture<Set<StaffUser>> future = new CompletableFuture<>();

        connection.executeQuery(Query.LOAD_ALL.get(table)).thenAcceptAsync(set -> {
            Set<StaffUser> out = new HashSet<>();
            try {
                while (set.next()) {
                    try {
                        String uuidString = set.getString("uuid");
                        String name = set.getString("name");
                        String rankName = set.getString("rank");
                        boolean staffChat = set.getBoolean("staff_chat");
                        boolean staffMessages = set.getBoolean("staff_messages");

                        UUID uniqueID = ParseUtil.parseUUID(uuidString);

                        if (uniqueID == null) {
                            plugin.getLogger().warning("Found user " + uuidString + ", " + name + " with invalid uuid.");
                            continue;
                        }

                        Rank rank = findRank(rankName);

                        StaffUser user = new StaffUser(uniqueID, rank);
                        user.setName(name);
                        user.setStaffChat(staffChat);
                        user.setStaffMessages(staffMessages);
                        out.add(user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            future.complete(out);
        });

        return future;
    }

    @NotNull
    private Rank findRank(String rankName) {
        Rank rank = plugin.getRankManager().getRank(rankName);

        if (rank == null) {
            rank = plugin.getRankManager().getRank("default");
            plugin.getLogger().warning("Rank " + rankName + " is unknown to this server. Using default.");
        }
        return rank;
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uniqueID) {
        return connection.execute(Query.DELETE_STAFF_USER.get(table), uniqueID.toString());
    }

    @Override
    public boolean finish() {
        // Nothing to do
        return true;
    }

    @Override
    public boolean initialize() {
        try {
            connection.connect();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }

        connection.execute(Query.CREATE_STAFF_TABLE.get(table));
        return true;
    }
}
