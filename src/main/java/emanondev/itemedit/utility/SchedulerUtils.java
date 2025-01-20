package emanondev.itemedit.utility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class for scheduling tasks in a Bukkit/Spigot/Folia environment.
 * Provides compatibility with Folia API if available, falling back to standard Bukkit scheduling otherwise.
 */
public final class SchedulerUtils {

    private SchedulerUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs a task asynchronously.
     *
     * @param plugin the plugin instance requesting the task.
     * @param task   the task to run asynchronously.
     */
    public static void runAsync(@NotNull Plugin plugin,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getAsyncScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "runNow",
                                    Plugin.class, plugin,
                                    Consumer.class, taskConsumer));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a task synchronously.
     *
     * @param plugin the plugin instance requesting the task.
     * @param task   the task to run synchronously.
     */
    public static void run(@NotNull Plugin plugin,
                           @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getAsyncScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "runNow",
                                    Plugin.class, plugin,
                                    Consumer.class, taskConsumer));
            return;
        }
        Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * Schedules a task to run later.
     *
     * @param plugin     the plugin instance requesting the task.
     * @param delayTicks the delay in ticks before the task is executed.
     * @param task       the task to run after the delay.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getAsyncScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "runDelayed",
                                    Plugin.class, plugin, Consumer.class,
                                    taskConsumer, long.class, delayTicks * 50L,
                                    TimeUnit.class, TimeUnit.MILLISECONDS));
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    /**
     * Runs a task asynchronously, associated with a specific location.
     *
     * @param plugin   the plugin instance requesting the task.
     * @param location the location associated with the task.
     * @param task     the task to run asynchronously.
     */
    public static void runAsync(@NotNull Plugin plugin,
                                @NotNull Location location,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getRegionScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "run",
                                    Plugin.class, plugin,
                                    Location.class, location,
                                    Consumer.class, taskConsumer));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a task synchronously, associated with a specific location.
     *
     * @param plugin   the plugin instance requesting the task.
     * @param location the location associated with the task.
     * @param task     the task to run synchronously.
     */
    public static void run(@NotNull Plugin plugin,
                           @NotNull Location location,
                           @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getRegionScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "run",
                                    Plugin.class, plugin,
                                    Location.class, location,
                                    Consumer.class, taskConsumer));
            return;
        }
        Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * Schedules a task to run later, associated with a specific location.
     *
     * @param plugin     the plugin instance requesting the task.
     * @param location   the location associated with the task.
     * @param delayTicks the delay in ticks before the task is executed.
     * @param task       the task to run after the delay.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @NotNull Location location,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(plugin.getServer(), "getRegionScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "runDelayed",
                                    Plugin.class, plugin,
                                    Location.class, location,
                                    Consumer.class, taskConsumer,
                                    long.class, delayTicks));
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    /**
     * Runs a task asynchronously, associated with a specific player.
     *
     * @param plugin the plugin instance requesting the task.
     * @param player the player associated with the task.
     * @param task   the task to run asynchronously.
     */
    public static void runAsync(@NotNull Plugin plugin,
                                @NotNull Player player,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(player, "getScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "run",
                                    Plugin.class, plugin,
                                    Consumer.class, taskConsumer,
                                    Runnable.class, null));
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a task synchronously, associated with a specific player.
     *
     * @param plugin the plugin instance requesting the task.
     * @param player the player associated with the task.
     * @param task   the task to run synchronously.
     */
    public static void run(@NotNull Plugin plugin,
                           @NotNull Player player,
                           @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(player, "getScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "run",
                                    Plugin.class, plugin,
                                    Consumer.class, taskConsumer,
                                    Runnable.class, null));
            return;
        }
        Bukkit.getScheduler().runTask(plugin, task);
    }

    /**
     * Schedules a task to run later, associated with a specific player.
     *
     * @param plugin     the plugin instance requesting the task.
     * @param player     the player associated with the task.
     * @param delayTicks the delay in ticks before the task is executed.
     * @param task       the task to run after the delay.
     */
    public static void runLater(@NotNull Plugin plugin,
                                @NotNull Player player,
                                @Range(from = 1L, to = Long.MAX_VALUE) long delayTicks,
                                @NotNull Runnable task) {
        if (VersionUtils.hasFoliaAPI()) {
            foliaSchedulerInvoker(player, "getScheduler", task,
                    (scheduler, taskConsumer) ->
                            ReflectionUtils.invokeMethod(scheduler, "runDelayed",
                                    Plugin.class, plugin,
                                    Consumer.class, taskConsumer,
                                    Runnable.class, null,
                                    long.class, delayTicks));
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }

    /**
     * Invokes a Folia scheduler method using reflection.
     *
     * @param from          the source object from which to obtain the scheduler.
     * @param schedulerName the name of the scheduler method to invoke.
     * @param task          the task to be executed.
     * @param invoke        the method to execute the task on the scheduler.
     */
    private static void foliaSchedulerInvoker(Object from,
                                              String schedulerName,
                                              Runnable task,
                                              BiConsumer<Object, Consumer<?>> invoke) {
        Object scheduler = ReflectionUtils.invokeMethod(from, schedulerName);
        Consumer<?> taskConsumer = (scheduledTask -> task.run());
        invoke.accept(scheduler, taskConsumer);
    }
}

