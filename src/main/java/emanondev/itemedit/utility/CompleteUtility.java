package emanondev.itemedit.utility;

import emanondev.itemedit.aliases.IAliasSet;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CompleteUtility {

    private static final int MAX_COMPLETES = 200;

    private CompleteUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Completes a prefix based on an enum class. This method returns a list of enum constant names
     * (in lowercase) that start with the provided prefix. The comparison is case-insensitive.
     *
     * @param prefix    The prefix to match against the enum constant names.
     * @param enumClass The enum class to extract the values from.
     * @param <T>       The type of the enum.
     * @return A list of matching enum constant names (in lowercase).
     */
    @NotNull
    public static <T extends Enum<T>> List<String> complete(@NotNull String prefix,
                                                            @NotNull Class<T> enumClass) {
        prefix = prefix.toUpperCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T el : enumClass.getEnumConstants()) {
            if (el.toString().startsWith(prefix)) {
                results.add(el.toString().toLowerCase(Locale.ENGLISH));
                c++;
                if (c > MAX_COMPLETES) {
                    return results;
                }
            }
        }
        return results;
    }

    /**
     * Completes a prefix based on an enum class with an additional filtering predicate.
     * This method returns a list of enum constant names (in lowercase) that start with the provided prefix
     * and satisfy the predicate condition. The comparison is case-insensitive.
     *
     * @param prefix    The prefix to match against the enum constant names.
     * @param type      The enum class to extract the values from.
     * @param predicate A predicate used to filter the enum constants.
     * @param <T>       The type of the enum.
     * @return A list of matching enum constant names (in lowercase) that satisfy the predicate.
     */
    @NotNull
    public static <T extends Enum<T>> List<String> complete(@NotNull String prefix,
                                                            @NotNull Class<T> type,
                                                            @NotNull Predicate<T> predicate) {
        prefix = prefix.toUpperCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T el : type.getEnumConstants())
            if (predicate.test(el) && el.toString().startsWith(prefix)) {
                results.add(el.toString().toLowerCase(Locale.ENGLISH));
                c++;
                if (c > MAX_COMPLETES) {
                    return results;
                }
            }
        return results;
    }

    /**
     * Completes a prefix based on a list of strings. This method returns a list of strings from the provided
     * list that start with the given prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the strings in the list.
     * @param list   The list of strings to search through.
     * @return A list of matching strings that start with the prefix.
     */
    @NotNull
    public static List<String> complete(@NotNull String prefix,
                                        String... list) {
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES) {
                    return results;
                }
            }
        return results;
    }

    /**
     * Completes a prefix based on a collection of strings. This method returns a list of strings from the
     * provided collection that start with the given prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the strings in the collection.
     * @param list   The collection of strings to search through.
     * @return A list of matching strings that start with the prefix.
     */
    @NotNull
    public static List<String> complete(@NotNull String prefix,
                                        @NotNull Collection<String> list) {
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES) {
                    return results;
                }
            }
        return results;
    }

    /**
     * Completes a prefix based on a collection of objects, using a provided converter function to extract
     * a string value from each object. This method returns a list of matching strings that start with the prefix.
     * The comparison is case-insensitive.
     *
     * @param prefix    The prefix to match against the strings derived from the objects.
     * @param list      The collection of objects to search through.
     * @param converter A function used to convert each object to a string for comparison.
     * @param <T>       The type of the objects in the collection.
     * @return A list of matching strings derived from the objects that start with the prefix.
     */
    @NotNull
    public static <T> List<String> complete(@NotNull String prefix,
                                            @NotNull Iterable<T> list,
                                            @NotNull Function<T, String> converter) {
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T value : list) {
            String textValue = converter.apply(value);
            if (textValue == null) { //skip nulls
                continue;
            }
            if (textValue.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                results.add(textValue);
                c++;
                if (c > MAX_COMPLETES) {
                    return results;
                }
            }
        }
        return results;
    }

    /**
     * Completes a prefix based on the names of online players in the game. This method returns a list of player names
     * that start with the provided prefix. The comparison is case-insensitive.
     *
     * @param prefix The prefix to match against the player names.
     * @return A list of player names that start with the prefix.
     */
    @NotNull
    public static List<String> completePlayers(@NotNull String prefix) {
        ArrayList<String> names = new ArrayList<>();
        final String text = prefix.toLowerCase(Locale.ENGLISH);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(text)) {
                names.add(p.getName());
            }
        });
        return names;
    }

    /**
     * Completes a prefix based on the aliases in an alias set. This method returns a list of aliases from the
     * provided alias set that start with the prefix. The comparison is case-insensitive.
     *
     * @param prefix  The prefix to match against the aliases in the alias set.
     * @param aliases The alias set containing the aliases to search through.
     * @return A list of matching aliases that start with the prefix.
     */
    @NotNull
    public static List<String> complete(@NotNull String prefix,
                                        @Nullable IAliasSet<?> aliases) {
        return complete(prefix,aliases,null);
    }

    /**
     * Completes a prefix based on the aliases in an alias set. This method returns a list of aliases from the
     * provided alias set that start with the prefix. The comparison is case-insensitive.
     *
     * @param prefix  The prefix to match against the aliases in the alias set.
     * @param aliases The alias set containing the aliases to search through.
     * @return A list of matching aliases that start with the prefix.
     */
    @NotNull
    public static <T> List<String> complete(@NotNull String prefix,
                                            @Nullable IAliasSet<T> aliases,
                                            @Nullable Predicate<T> filter) {
        if (aliases == null) {
            return Collections.emptyList();
        }
        ArrayList<String> results = new ArrayList<>();
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        int c = 0;
        for (String alias : aliases.getAliases()) {
            if (filter != null && !filter.test(aliases.convertAlias(alias))) {
                continue;
            }
            if (!alias.startsWith(prefix)) {
                continue;
            }
            results.add(alias);
            c++;
            if (c > MAX_COMPLETES) {
                return results;
            }
        }
        return results;
    }
}
