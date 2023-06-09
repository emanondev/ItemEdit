package emanondev.itemedit.storage.yaml;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class YmlPlayerStorage implements PlayerStorage {

    private final YMLConfig database = ItemEdit.get()
            .getConfig("database" + File.separatorChar + "player-database.yml");

    private String getBasePath(OfflinePlayer p) {
        return storeByUUID() ? p.getUniqueId().toString() : p.getName();
    }

    @Override
    public ItemStack getItem(@NotNull OfflinePlayer p, @NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        ItemStack item = database.getItemStack(getBasePath(p) + "." + id + ".item", null);
        return item == null ? null : item.clone();
    }

    @Override
    public void setItem(@NotNull OfflinePlayer p, @NotNull String id, @NotNull ItemStack item) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        if (item.getType() == Material.AIR)
            throw new IllegalArgumentException();
        item.setAmount(1);
        database.set(getBasePath(p) + "." + id + ".item", item);
        database.save();
    }

    @Override
    public void remove(@NotNull OfflinePlayer p, @NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        database.set(getBasePath(p) + "." + id, null);
        database.save();
    }

    @Override
    public void clear(@NotNull OfflinePlayer p) {
        database.set(getBasePath(p), null);
        database.save();
    }

    @Override
    public @NotNull Set<String> getIds(@NotNull OfflinePlayer p) {
        return database.getKeys(getBasePath(p));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull Set<OfflinePlayer> getPlayers() {
        Set<String> playersData = database.getKeys(false);
        Set<OfflinePlayer> players = new HashSet<>();
        boolean uuid = storeByUUID();
        for (String val : playersData) {
            if (uuid)
                players.add(Bukkit.getOfflinePlayer(UUID.fromString(val)));
            else
                players.add(Bukkit.getOfflinePlayer(val));
        }
        return players;
    }

}
