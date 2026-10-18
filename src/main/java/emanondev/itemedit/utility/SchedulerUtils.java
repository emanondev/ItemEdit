package emanondev.itemedit.utility;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Helpers for scheduling tasks on Paper and Folia.
 * Use the player or location overloads when the task accesses that entity or region.
 */
public final class SchedulerUtils {

    private SchedulerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs a task asynchronously. The task must not access Bukkit world or entity state.
     */
    public static void runAsync(@NotNull Plugin plugin, @NotNull Runnable task) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
    }

    /**
     * Runs a task on the global region.
     */
    public static void run(@NotNull Plugin plugin, @NotNull Runnable task) {
        plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
    }

    /**
     * Schedules a task on the global region after a number of ticks.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin,
                scheduledTask -> task.run(), delayTicks);
    }

    /**
     * Runs a task on the region that owns the location.
     */
    public static void run(@NotNull Plugin plugin, @NotNull Location location, @NotNull Runnable task) {
        plugin.getServer().getRegionScheduler().run(plugin, location, scheduledTask -> task.run());
    }

    /**
     * Schedules a task on the region that owns the location after a number of ticks.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @NotNull Location location,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        plugin.getServer().getRegionScheduler().runDelayed(plugin, location,
                scheduledTask -> task.run(), delayTicks);
    }

    /**
     * Runs a task on the scheduler owned by the player.
     */
    public static void run(@NotNull Plugin plugin,
                           @NotNull Player player,
                           @NotNull Runnable task) {
        player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
    }

    /**
     * Schedules a task on the scheduler owned by the player after a number of ticks.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @NotNull Player player,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
    }
}
