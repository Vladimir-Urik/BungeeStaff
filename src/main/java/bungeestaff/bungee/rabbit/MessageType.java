package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import org.jetbrains.annotations.Nullable;

public enum MessageType {

    STAFF_CHAT((plugin, message) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    REPORT((plugin, message) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    REQUEST((plugin, message) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    JOIN((plugin, message) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    LEAVE((plugin, message) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    BROADCAST((plugin, message) -> {
        plugin.getBroadcastManager().broadcastRaw(message, false);
    });

    private final MessageDispatcher dispatcher;

    MessageType(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void dispatch(BungeeStaffPlugin plugin, String message) {
        dispatcher.dispatch(plugin, message);
    }

    @Nullable
    public static MessageType fromString(String str) {
        try {
            return valueOf(str.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public interface MessageDispatcher {
        void dispatch(BungeeStaffPlugin plugin, String message);
    }
}
