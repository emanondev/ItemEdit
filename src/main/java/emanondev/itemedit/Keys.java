package emanondev.itemedit;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Keys {

    private static NamespacedKey craftKey(String postfix) {
        return new NamespacedKey(NamespacedKey.MINECRAFT, postfix);
    }

    public static abstract class EffectType implements Keyed {

        public static final NamespacedKey APPLY_EFFECTS = craftKey("apply_effects");
        public static final NamespacedKey REMOVE_EFFECTS = craftKey("remove_effects");
        public static final NamespacedKey CLEAR_ALL_EFFECTS = craftKey("clear_all_effects");
        public static final NamespacedKey TELEPORT_RANDOMLY = craftKey("teleport_randomly");
        public static final NamespacedKey PLAY_SOUND = craftKey("play_sound");
    }

    public static abstract class Component implements Keyed {

        public static final NamespacedKey FOOD = craftKey("food");
        public static final NamespacedKey CONSUMABLE = craftKey("consumable");
        public static final NamespacedKey USE_REMAINDER = craftKey("use_remainder");
        public static final NamespacedKey CROSS_VERSION_CONSUMABLE = Util.isVersionUpTo(1, 21, 1) ? FOOD : CONSUMABLE;
    }


    private static class KeyRegistry<T extends Keyed> implements Registry<T> {
        private final LinkedHashMap<NamespacedKey, T> values = new LinkedHashMap<>();

        public KeyRegistry(Collection<T> collection) {
            collection.forEach((v) -> this.values.put(v.getKey(), v));
        }

        public KeyRegistry(T... collection) {
            for (T value : collection)
                this.values.put(value.getKey(), value);
        }

        @Nullable
        @Override
        public T get(@NotNull NamespacedKey namespacedKey) {
            return values.get(namespacedKey);
        }

        @NotNull
        @Override
        public T getOrThrow(@NotNull NamespacedKey namespacedKey) {
            if (!values.containsKey(namespacedKey))
                throw new IllegalArgumentException();
            return values.get(namespacedKey);
        }

        @NotNull
        @Override
        public Stream<T> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return values.values().iterator();
        }
    }
}
