package emanondev.itemedit;

import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CooldownAPI {

    private final YMLConfig conf;
    private final Map<UUID, Map<String, Long>> cooldowns =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();

    CooldownAPI(@NotNull APlugin plugin) {
        long now = System.currentTimeMillis();
        conf = plugin.getConfig("cooldownData.yml");
        for (String id : conf.getKeys(false)) {
            Map<String, Long> map = VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
            cooldowns.put(UUID.fromString(id), map);
            for (String cooldownId : conf.getKeys(id)) {
                try {
                    long value = conf.getLong(id + "." + cooldownId, 0L);
                    if (value > now) {
                        map.put(cooldownId, value);
                    }
                } catch (Exception e) {
                    plugin.log("Corrupted path value for cooldown data on ");
                    e.printStackTrace();
                }
            }
        }
    }

    void save() {
        long now = System.currentTimeMillis();
        conf.getKeys(false).forEach(path -> conf.set(path, null));
        for (UUID uuid : cooldowns.keySet()) {
            Map<String, Long> values = cooldowns.get(uuid);
            for (String id : values.keySet()) {
                if (values.get(id) > now) {
                    conf.getLong(uuid.toString() + "." + id, values.get(id));
                }
            }
        }
        conf.save();
    }

    /**
     * Sets a cooldown for a block with a specified duration.
     *
     * @param block      the block whose cooldown is to be set.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration of the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(@NotNull Block block,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        setCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId, duration, timeUnit);
    }

    /**
     * Adds to the cooldown for a block.
     *
     * @param block      the block whose cooldown is to be added to.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to add to the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(@NotNull Block block,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        addCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId, duration, timeUnit);
    }

    /**
     * Reduces the cooldown for a block.
     *
     * @param block      the block whose cooldown is to be reduced.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to reduce from the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(@NotNull Block block,
                               @NotNull String cooldownId,
                               @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               @NotNull TimeUnit timeUnit) {
        reduceCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId, duration, timeUnit);
    }

    /**
     * Removes a cooldown for a block.
     *
     * @param block      the block whose cooldown is to be removed.
     * @param cooldownId the ID of the cooldown.
     */
    public void removeCooldown(@NotNull Block block,
                               @NotNull String cooldownId) {
        removeCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId);
    }

    /**
     * Checks if the given block has an active cooldown for the specified cooldown ID.
     *
     * @param block      The offline block to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the block has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(@NotNull Block block,
                               @NotNull String cooldownId) {
        return hasCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId);
    }

    /**
     * Retrieves the cooldown for a block and cooldown ID, converted to a specified time unit.
     *
     * @param block      the block whose cooldown is to be retrieved.
     * @param cooldownId the ID of the cooldown.
     * @param timeUnit   the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(@NotNull Block block,
                            @NotNull String cooldownId,
                            @NotNull TimeUnit timeUnit) {
        return getCooldown(new UUID(((long) block.getX() << 32) | (long) block.getY(),
                        ((long) block.getZ() << 32) | (long) block.getWorld().getName().hashCode()),
                cooldownId, timeUnit);
    }

    /**
     * Sets a cooldown for a player with a specified duration.
     *
     * @param player     the player whose cooldown is to be set.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration of the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(@NotNull OfflinePlayer player,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        setCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Adds to the cooldown for a player.
     *
     * @param player     the player whose cooldown is to be added to.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to add to the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(@NotNull OfflinePlayer player,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        addCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Reduces the cooldown for a player.
     *
     * @param player     the player whose cooldown is to be reduced.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to reduce from the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(@NotNull OfflinePlayer player,
                               @NotNull String cooldownId,
                               @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               @NotNull TimeUnit timeUnit) {
        reduceCooldown(player.getUniqueId(), cooldownId, duration, timeUnit);
    }

    /**
     * Removes a cooldown for a player.
     *
     * @param player     the player whose cooldown is to be removed.
     * @param cooldownId the ID of the cooldown.
     */
    public void removeCooldown(@NotNull OfflinePlayer player,
                               @NotNull String cooldownId) {
        removeCooldown(player.getUniqueId(), cooldownId);
    }

    /**
     * Checks if the given player has an active cooldown for the specified cooldown ID.
     *
     * @param player     The offline player to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the player has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(@NotNull OfflinePlayer player,
                               @NotNull String cooldownId) {
        return hasCooldown(player.getUniqueId(), cooldownId);
    }

    /**
     * Retrieves the cooldown for a player and cooldown ID, converted to a specified time unit.
     *
     * @param player     the player whose cooldown is to be retrieved.
     * @param cooldownId the ID of the cooldown.
     * @param timeUnit   the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(@NotNull OfflinePlayer player,
                            @NotNull String cooldownId,
                            @NotNull TimeUnit timeUnit) {
        return getCooldown(player.getUniqueId(), cooldownId, timeUnit);
    }

    /**
     * Sets a cooldown for a UUID with a specified duration.
     *
     * @param uuid       the UUID.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration of the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void setCooldown(@NotNull UUID uuid,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        if (duration <= 0 && cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).remove(cooldownId);
        } else {
            cooldowns.computeIfAbsent(uuid, k ->
                    VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>());
            cooldowns.get(uuid).put(cooldownId,
                    System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(duration, timeUnit));
        }
    }

    /**
     * Adds to the cooldown for a UUID.
     *
     * @param uuid       the UUID.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to add to the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void addCooldown(@NotNull UUID uuid,
                            @NotNull String cooldownId,
                            @Range(from = 0, to = Long.MAX_VALUE) long duration,
                            @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        setCooldown(uuid, cooldownId, getCooldown(uuid, cooldownId, TimeUnit.MILLISECONDS)
                + TimeUnit.MILLISECONDS.convert(duration, timeUnit), TimeUnit.MILLISECONDS);
    }

    /**
     * Reduces the cooldown for a UUID.
     *
     * @param uuid       the UUID.
     * @param cooldownId the ID of the cooldown.
     * @param duration   the duration to reduce from the cooldown.
     * @param timeUnit   the time unit of the duration.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public void reduceCooldown(@NotNull UUID uuid,
                               @NotNull String cooldownId,
                               @Range(from = 0, to = Long.MAX_VALUE) long duration,
                               @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException();
        }
        setCooldown(uuid, cooldownId, getCooldown(uuid, cooldownId, TimeUnit.MILLISECONDS)
                - TimeUnit.MILLISECONDS.convert(duration, timeUnit), TimeUnit.MILLISECONDS);
    }

    /**
     * Removes a cooldown for a UUID.
     *
     * @param uuid       the UUID whose cooldown is to be removed.
     * @param cooldownId the ID of the cooldown.
     */
    public void removeCooldown(@NotNull UUID uuid,
                               @NotNull String cooldownId) {
        if (cooldowns.get(uuid) != null) {
            cooldowns.get(uuid).remove(cooldownId);
        }
    }

    /**
     * Checks if the player (by UUID) has an active cooldown for the specified cooldown ID.
     *
     * @param player     The UUID of the player to check.
     * @param cooldownId The ID of the cooldown.
     * @return {@code true} if the player has an active cooldown, {@code false} otherwise.
     */
    public boolean hasCooldown(@NotNull UUID player,
                               @NotNull String cooldownId) {
        return getCooldown(player, cooldownId, TimeUnit.MILLISECONDS) > 0;
    }

    /**
     * Retrieves the cooldown for a UUID and cooldown ID, converted to a specified time unit.
     *
     * @param uuid       the UUID.
     * @param cooldownId the ID of the cooldown.
     * @param timeUnit   the time unit to convert the cooldown to.
     * @return the cooldown duration in the specified time unit, or 0 if no cooldown is active.
     * @throws UnsupportedOperationException if the time unit is less than {@link TimeUnit#MILLISECONDS}.
     */
    public long getCooldown(@NotNull UUID uuid,
                            @NotNull String cooldownId,
                            @NotNull TimeUnit timeUnit) {
        if (timeUnit.compareTo(TimeUnit.MILLISECONDS) < 0) {
            throw new UnsupportedOperationException("Time unit must be at least MILLISECONDS.");
        }
        long cooldownMS = cooldowns.containsKey(uuid) ?
                Math.max(0L, cooldowns.get(uuid).getOrDefault(cooldownId, 0L) - System.currentTimeMillis()) :
                0L;
        return timeUnit.convert(cooldownMS, TimeUnit.MILLISECONDS);
    }
}
