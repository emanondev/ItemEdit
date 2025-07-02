package emanondev.itemedit;

import emanondev.itemedit.utility.VersionUtils;
import org.jetbrains.annotations.NotNull;

public final class UtilLegacy {

    /**
     * This method accepts <code>infinite</code>,<code>∞</code>, <code>instant</code> or any number as valid input.<br>
     * On versions before 1.19.4, infinite is turned to <code>Integer.MAX_VALUE</code>.<br><br>
     * In API versions 1.19.3 and earlier, there is no infinite value for PotionEffects.<br>
     * In API versions 1.19.4 and later, -1 is the value for infinite duration.
     *
     * @param value The raw value to be interpreted, values below <code>0</code> are treated as infinite
     * @throws NumberFormatException When value cannot be interpreted to a valid value
     */
    public static int readPotionEffectDurationSecondsToTicks(@NotNull String value) {
        double durationSecs = readPotionDurationSeconds(value);

        // Zero should be 1 tick.
        if(durationSecs == 0) {
            return 1;
        }

        // Seconds to ticks : *20
        if(durationSecs > 0) {
            return (int) (durationSecs * 20);
        }

        // Infinite duration
        return VersionUtils.isVersionAfter(1, 19, 4) ? Integer.MAX_VALUE : -1;
    }

    /**
     * This method accepts <code>infinite</code>,<code>∞</code>, <code>instant</code> or any number as valid input.<br>
     * It transforms a string into a floating-point value in seconds.
     * @param value The raw value to be interpreted, values below <code>0</code> are treated as infinite.
     * @return a duration, in seconds.
     * @throws NumberFormatException When value cannot be interpreted to a valid double.
     */
    private static double readPotionDurationSeconds(@NotNull String value) {
        // Infinite duration.
        if(value.equalsIgnoreCase("infinite") || value.equalsIgnoreCase("∞")) {
            return -1;
        }
        // zero-duration.
        if(value.equalsIgnoreCase("instant")) {
            return 0;
        }
        // Normal duration.
        return Double.parseDouble(value);
    }
}
