package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import org.jetbrains.annotations.Nullable;

public enum MessageType {

    STAFF((plugin, message, serverId) -> {
        plugin.getStaffManager().sendRawMessage(message, null);
    }),

    PUBLIC((plugin, message, serverId) -> {
        plugin.getBroadcastManager().broadcastRaw(message, false);
    }),

    UPDATE_USERS((plugin, message, serverId) -> {
        plugin.getMessagingManager().processUserUpdate(serverId, message);
    });

    private final MessageDispatcher dispatcher;

    MessageType(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void dispatch(BungeeStaffPlugin plugin, String message, String serverId) {
        dispatcher.dispatch(plugin, message, serverId);
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
        void dispatch(BungeeStaffPlugin plugin, String message, String serverId);
    }
}
