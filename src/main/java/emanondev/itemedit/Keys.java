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

    public static class Component implements Keyed {
        private final NamespacedKey key;

        private Component(String postfix) {
            this.key = new NamespacedKey(NamespacedKey.MINECRAFT, postfix);
        }

        @NotNull
        @Override
        public NamespacedKey getKey() {
            return key;
        }

        @NotNull
        @Override
        public String toString() {
            return key.toString();
        }

        public static Component FOOD = new Component("food");
        public static Component CONSUMABLE = new Component("consumable");
        public static Component USE_REMAINDER = new Component("use_remainder");
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
