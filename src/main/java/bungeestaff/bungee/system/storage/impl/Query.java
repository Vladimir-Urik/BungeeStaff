package bungeestaff.bungee.system.storage.impl;

public enum Query {

    LOAD_STAFF_USER("SELECT `name`, `rank` FROM `%table%` WHERE `uuid` = ?"),

    LOAD_ALL("SELECT `uuid`, `name`, `rank`, `staff_chat`, `staff_messages` FROM `%table%`"),

    SAVE_STAFF_USER("INSERT INTO `%table%` (`uuid`, `name`, `rank`, `staff_chat`, `staff_messages`) " +
            "VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "`uuid` = ?, `name` = ?, `rank` = ?, `staff_chat` = ?, `staff_messages` = ?"),

    DELETE_STAFF_USER("DELETE FROM `%table%` WHERE `uuid` = ?");

    private final String query;

    Query(String query) {
        this.query = query;
    }

    public String get(String table) {
        return query.replace("%table%", table);
    }
}
