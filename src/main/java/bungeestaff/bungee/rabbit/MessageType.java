package bungeestaff.bungee.rabbit;

import bungeestaff.bungee.BungeeStaffPlugin;
import bungeestaff.bungee.rabbit.cache.CachedUser;
import bungeestaff.bungee.system.staff.StaffUser;
import bungeestaff.bungee.util.ParseUtil;
import com.google.common.base.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public enum MessageType {

    //TODO Ditch mass updates and replace with single user leave&join

    // A staff only message (staff chat, join, leave, connect)
    STAFF_MESSAGE((plugin, message, serverId) -> {
        plugin.getStaffManager().sendStaffMessageRaw(message);
    }),

    // Sending a public message (broadcast)
    PUBLIC_MESSAGE((plugin, message, serverId) -> {
        plugin.getBroadcastManager().broadcastRaw(message, false);
    }),

    UPDATE_USERS((plugin, message, serverId) -> {
        if (Strings.isNullOrEmpty(message))
            return;

        Set<CachedUser> users = ParseUtil.deserializeSet(message.trim(), CachedUser::deserialize);
        plugin.getUserCache().importUsers(serverId, users);
    }),

    STAFF_ADD((plugin, message, serverId) -> {
        StaffUser user = StaffUser.deserialize(plugin, message);
        if (user == null)
            return;
        plugin.getStaffManager().createStaffUser(user, false);
    }),

    STAFF_REMOVE((plugin, message, serverId) -> {
        StaffUser user = plugin.getStaffManager().getUser(message.trim());
        if (user == null)
            return;
        plugin.getStaffManager().removeUser(user, false);
    }),

    STAFF_TSM((plugin, message, serverId) -> {
        StaffUser user = plugin.getStaffManager().getUser(message.split(";")[0]);

        if (user == null)
            return;

        boolean bool = Boolean.parseBoolean(message.split(";")[1]);
        user.setStaffMessages(bool);
    }),

    STAFF_SC((plugin, message, serverId) -> {
        StaffUser user = plugin.getStaffManager().getUser(message.split(";")[0]);

        if (user == null)
            return;

        boolean bool = Boolean.parseBoolean(message.split(";")[1]);
        user.setStaffChat(bool);
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
