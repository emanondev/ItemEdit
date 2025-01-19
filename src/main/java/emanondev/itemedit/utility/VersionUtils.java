package emanondev.itemedit.utility;

import org.bukkit.Bukkit;


/**
 * Utility class for managing server version information and compatibility checks.
 */
public final class VersionUtils {

    private static final String[] VERSION_PARTS = safeSplitVersion();
    public static final int GAME_MAIN_VERSION = Integer.parseInt(VERSION_PARTS[0]);
    public static final int GAME_VERSION = Integer.parseInt(VERSION_PARTS[1]);
    public static final int GAME_SUB_VERSION = VERSION_PARTS.length < 3 ? 0 : Integer.parseInt(VERSION_PARTS[2]);
    private static final boolean hasPaper = ReflectionUtils
            .isClassPresent("com.destroystokyo.paper.VersionHistoryManager$VersionData");
    private static final boolean hasFolia = ReflectionUtils
            .isClassPresent("io.papermc.paper.threadedregions.RegionizedServer");
    private static final boolean hasPurpur = ReflectionUtils
            .isClassPresent("org.purpurmc.purpur.event.PlayerAFKEvent");

    private VersionUtils() {
        throw new UnsupportedOperationException("VersionUtils is a utility class and cannot be instantiated.");
    }

    public static String getVersionType() {
        return hasFoliaAPI() ? "Folia" :
                (hasPurpurAPI() ? "Purpur" :
                        (hasPaperAPI() ? "Paper" : "Spigot"));
    }

    public static String getVersionNumber() {
        return GAME_MAIN_VERSION + "." + GAME_VERSION + "." + GAME_SUB_VERSION;
    }

    public static String getVersion() {
        return getVersionType() + " " + getVersionNumber();
    }

    /**
     * Safely splits the Bukkit version string into components.
     *
     * @return an array of version components.
     */
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
     * @param mainVersion the main version (e.g., 1 in 1.9.4).
     * @param version the minor version (e.g., 9 in 1.9.4).
     * @return true if the current version is up to the specified version.
     */
    public static boolean isVersionUpTo(int mainVersion, int version) {
        return isVersionUpTo(mainVersion, version, 99);
    }

    /**
     * Checks if the current version is up to (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param mainVersion the main version.
     * @param version the minor version.
     * @param subVersion the sub version.
     * @return true if the current version is up to the specified version.
     */
    public static boolean isVersionUpTo(int mainVersion, int version, int subVersion) {
        if (GAME_MAIN_VERSION > mainVersion) return false;
        if (GAME_MAIN_VERSION < mainVersion) return true;
        if (GAME_VERSION > version) return false;
        if (GAME_VERSION < version) return true;
        return GAME_SUB_VERSION <= subVersion;
    }

    /**
     * Checks if the current version is after (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param mainVersion the main version.
     * @param version the minor version.
     * @return true if the current version is after the specified version.
     */
    public static boolean isVersionAfter(int mainVersion, int version) {
        return isVersionAfter(mainVersion, version, 0);
    }

    /**
     * Checks if the current version is after (or equal to) the specified version.
     * Inclusive comparison.
     *
     * @param mainVersion the main version.
     * @param version the minor version.
     * @param subVersion the sub version.
     * @return true if the current version is after the specified version.
     */
    public static boolean isVersionAfter(int mainVersion, int version, int subVersion) {
        if (GAME_MAIN_VERSION < mainVersion) return false;
        if (GAME_MAIN_VERSION > mainVersion) return true;
        if (GAME_VERSION < version) return false;
        if (GAME_VERSION > version) return true;
        return GAME_SUB_VERSION >= subVersion;
    }

    /**
     * Checks if the current version is within the specified range.
     * Inclusive comparison.
     *
     * @param mainVersionMin the minimum main version.
     * @param versionMin the minimum minor version.
     * @param mainVersionMax the maximum main version.
     * @param versionMax the maximum minor version.
     * @return true if the current version is within the range.
     */
    public static boolean isVersionInRange(int mainVersionMin,
                                           int versionMin,
                                           int mainVersionMax,
                                           int versionMax) {
        return isVersionInRange(mainVersionMin, versionMin, 0, mainVersionMax, versionMax, 99);
    }

    /**
     * Checks if the current version is within the specified range.
     * Inclusive comparison.
     *
     * @param mainVersionMin the minimum main version.
     * @param versionMin the minimum minor version.
     * @param subVersionMin the minimum sub version.
     * @param mainVersionMax the maximum main version.
     * @param versionMax the maximum minor version.
     * @param subVersionMax the maximum sub version.
     * @return true if the current version is within the range.
     */
    public static boolean isVersionInRange(int mainVersionMin,
                                           int versionMin,
                                           int subVersionMin,
                                           int mainVersionMax,
                                           int versionMax,
                                           int subVersionMax) {
        return isVersionAfter(mainVersionMin, versionMin, subVersionMin) &&
                isVersionUpTo(mainVersionMax, versionMax, subVersionMax);
    }

    /**
     * Checks if the Paper API is available.
     *
     * @return true if Paper API is present, false otherwise.
     */
    public static boolean hasPaperAPI() {
        return hasPaper;
    }

    /**
     * Checks if the Purpur API is available.
     *
     * @return true if Purpur API is present, false otherwise.
     */
    public static boolean hasPurpurAPI() {
        return hasPurpur;
    }

    /**
     * Checks if the Folia API is available.
     *
     * @return true if Folia API is present, false otherwise.
     */
    public static boolean hasFoliaAPI() {
        return hasFolia;
    }
}