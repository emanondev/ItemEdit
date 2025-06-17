package emanondev.itemedit.utility;

import org.bukkit.Bukkit;


/**
 * Utility class for managing server version information and compatibility checks.
 */
public final class VersionUtils {

    private static final String[] VERSION_PARTS = safeSplitVersion();

    /**
     * The major version of the game (e.g., the first number in the version string).
     */
    public static final int GAME_MAIN_VERSION = Integer.parseInt(VERSION_PARTS[0]);

    /**
     * The minor version of the game (e.g., the second number in the version string).
     */
    public static final int GAME_VERSION = Integer.parseInt(VERSION_PARTS[1]);

    /**
     * The patch version of the game, or 0 if the version string has fewer than three parts.
     */
    public static final int GAME_SUB_VERSION = VERSION_PARTS.length < 3 ? 0 : Integer.parseInt(VERSION_PARTS[2]);

    private static final boolean HAS_PAPER = ReflectionUtils
            .isClassPresent("com.destroystokyo.paper.VersionHistoryManager$VersionData");
    private static final boolean HAS_FOLIA = ReflectionUtils
            .isClassPresent("io.papermc.paper.threadedregions.RegionizedServer");
    private static final boolean HAS_PURPUR = ReflectionUtils
            .isClassPresent("org.purpurmc.purpur.event.PlayerAFKEvent");

    private VersionUtils() {
        throw new UnsupportedOperationException("VersionUtils is a utility class and cannot be instantiated.");
    }

    /**
     * Determines the type of server (e.g., Folia, Purpur, Paper, or Spigot).
     *
     * @return the type of server as a string.
     */
    public static String getVersionType() {
        return hasFoliaAPI() ? "Folia" :
                (hasPurpurAPI() ? "Purpur" :
                        (hasPaperAPI() ? "Paper" : "Spigot"));
    }

    /**
     * Constructs the full version number as a string in the format "main.minor.sub".
     *
     * @return the version number as a string.
     */
    public static String getVersionNumber() {
        return GAME_MAIN_VERSION + "." + GAME_VERSION + "." + GAME_SUB_VERSION;
    }

    /**
     * Constructs the full server version string, including type and version number.
     *
     * @return the full server version as a string.
     */
    public static String getVersion() {
        return getVersionType() + " " + getVersionNumber();
    }

    private static String[] safeSplitVersion() {
        try {
            return Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        } catch (Exception e) {
            throw new IllegalStateException("Invalid Bukkit version format: " + Bukkit.getBukkitVersion(), e);
        }
    }

    /**
     * Checks if the current version is up to (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param major the main version (e.g., 1 in 1.9.4).
     * @param minor     the minor version (e.g., 9 in 1.9.4).
     * @return true if the current version is up to the specified version.
     */
    public static boolean isVersionUpTo(int major,
                                        int minor) {
        return isVersionUpTo(major, minor, 99);
    }

    /**
     * Checks if the current version is up to (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param major the main version.
     * @param minor     the minor version.
     * @param patch  the sub version.
     * @return true if the current version is up to the specified version.
     */
    public static boolean isVersionUpTo(int major,
                                        int minor,
                                        int patch) {
        if (GAME_MAIN_VERSION > major) return false;
        if (GAME_MAIN_VERSION < major) return true;
        if (GAME_VERSION > minor) return false;
        if (GAME_VERSION < minor) return true;
        return GAME_SUB_VERSION <= patch;
    }

    /**
     * Checks if the current version is after (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param major the main version.
     * @param minor     the minor version.
     * @return true if the current version is after the specified version.
     */
    public static boolean isVersionAfter(int major,
                                         int minor) {
        return isVersionAfter(major, minor, 0);
    }

    /**
     * Checks if the current version is after (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param major the main version.
     * @param minor     the minor version.
     * @param patch  the sub version.
     * @return true if the current version is after the specified version.
     */
    public static boolean isVersionAfter(int major,
                                         int minor,
                                         int patch) {
        if (GAME_MAIN_VERSION < major) return false;
        if (GAME_MAIN_VERSION > major) return true;
        if (GAME_VERSION < minor) return false;
        if (GAME_VERSION > minor) return true;
        return GAME_SUB_VERSION >= patch;
    }

    /**
     * Checks if the current version is within the specified range.
     * Inclusive comparison.
     *
     * @param majorMin the minimum main version.
     * @param minorMin     the minimum minor version.
     * @param majorMax the maximum main version.
     * @param minorMax     the maximum minor version.
     * @return true if the current version is within the range.
     */
    public static boolean isVersionInRange(int majorMin,
                                           int minorMin,
                                           int majorMax,
                                           int minorMax) {
        return isVersionInRange(majorMin, minorMin, 0, majorMax, minorMax, 99);
    }

    /**
     * Checks if the current version is within the specified range.
     * Inclusive comparison.
     *
     * @param majorMin the minimum main version.
     * @param minorMin     the minimum minor version.
     * @param patchMin  the minimum sub version.
     * @param majorMax the maximum main version.
     * @param minorMax     the maximum minor version.
     * @param patchMax  the maximum sub version.
     * @return true if the current version is within the range.
     */
    public static boolean isVersionInRange(int majorMin,
                                           int minorMin,
                                           int patchMin,
                                           int majorMax,
                                           int minorMax,
                                           int patchMax) {
        return isVersionAfter(majorMin, minorMin, patchMin) &&
                isVersionUpTo(majorMax, minorMax, patchMax);
    }

    /**
     * Checks if the Paper API is available.
     *
     * @return true if Paper API is present, false otherwise.
     */
    public static boolean hasPaperAPI() {
        return HAS_PAPER;
    }

    /**
     * Checks if the Purpur API is available.
     *
     * @return true if Purpur API is present, false otherwise.
     */
    public static boolean hasPurpurAPI() {
        return HAS_PURPUR;
    }

    /**
     * Checks if the Folia API is available.
     *
     * @return true if Folia API is present, false otherwise.
     */
    public static boolean hasFoliaAPI() {
        return HAS_FOLIA;
    }
}