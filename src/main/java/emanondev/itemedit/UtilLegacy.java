package emanondev.itemedit;

import emanondev.itemedit.utility.VersionUtils;
import org.jetbrains.annotations.NotNull;

public final class UtilLegacy {

    /**
     * This method accepts <code>infinite</code>,<code>∞</code> or any number as valid input.<br>
     * On versions before 1.19.4, infinite is turned to <code>Integer.MAX_VALUE</code>.<br><br>
     * In API versions 1.19.3 and earlier, there is no infinite value for PotionEffects.<br>
     * In API versions 1.19.4 and later, -1 is the value for infinite duration.
     *
     * @param value The raw value to be interpreted, values below <code>0</code> are treated as infinite
     * @throws NumberFormatException When value cannot be interpreted to a valid value
     */
    public static int readPotionEffectDurationSecondsToTicks(@NotNull String value) {
        int duration = (value.equalsIgnoreCase("infinite")
                || (value.equalsIgnoreCase("∞"))) ?
                -1 : (Integer.parseInt(value));
        if (duration >= 0) {
            duration *= 20; //to ticks
        } else if (!VersionUtils.isVersionAfter(1, 19, 4)) {
            duration = Integer.MAX_VALUE;
        } else {
            duration = -1;
        }
        return duration;
    }
}
