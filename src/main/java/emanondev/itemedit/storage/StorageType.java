package emanondev.itemedit.storage;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum StorageType {
    YAML,
    MONGODB;

    public static @NotNull Optional<StorageType> byName(@NotNull String name) {
        return Arrays.stream(values()).filter(storageType -> storageType.name().equalsIgnoreCase(name)).findAny();
    }
}
