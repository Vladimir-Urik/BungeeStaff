package bungeestaff.bungee.util;

import bungeestaff.bungee.system.Serializable;
import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @NotNull
    public <T> Set<T> deserializeSet(String input, Function<String, T> deserializer) {
        Set<T> out = new HashSet<>();
        for (String arg : input.split(",")) {
            T obj = deserializer.apply(arg);
            out.add(obj);
        }
        return out;
    }

    // Serialize a collection of objects
    public <T extends Serializable> String serializeCollection(Collection<T> collection) {
        return collection.stream()
                .map(Serializable::serialize)
                .collect(Collectors.joining(","));
    }
}
