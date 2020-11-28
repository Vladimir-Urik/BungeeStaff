package bungeestaff.bungee.util;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@UtilityClass
public class ParseUtil {

    @Nullable
    public UUID parseUUID(String str) {

        if (Strings.isNullOrEmpty(str))
            return null;

        try {
            return UUID.fromString(str.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
