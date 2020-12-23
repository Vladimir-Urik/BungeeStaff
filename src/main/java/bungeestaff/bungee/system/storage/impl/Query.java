package bungeestaff.bungee.system.storage.impl;

public enum Query {

    CREATE_STAFF_TABLE("CREATE TABLE IF NOT EXISTS `%table%` (\n" +
            "`uuid` VARCHAR(40) NOT NULL,\n" +
            "`name` VARCHAR(16),\n" +
            "`rank` VARCHAR(16),\n" +
            "`staff_chat` BOOLEAN,\n" +
            "`staff_messages` BOOLEAN,\n" +
            "PRIMARY KEY (`uuid`),\n" +
            "UNIQUE (`uuid`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"),

    DELETE_STAFF_USER("DELETE FROM `%table%` WHERE `uuid` = ?"),

    LOAD_ALL("SELECT `uuid`, `name`, `rank`, `staff_chat`, `staff_messages` FROM `%table%`"),

    LOAD_STAFF_USER("SELECT `name`, `rank`, `staff_chat`, `staff_messages` FROM `%table%` WHERE `uuid` = ?"),

    SAVE_STAFF_USER("INSERT INTO `%table%` (`uuid`, `name`, `rank`, `staff_chat`, `staff_messages`) " +
            "VALUES (?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "`uuid` = ?, `name` = ?, `rank` = ?, `staff_chat` = ?, `staff_messages` = ?");

    private final String query;

    Query(String query) {
        this.query = query;
    }

    public String get(String table) {
        return query.replace("%table%", table);
    }
}
