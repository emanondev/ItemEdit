package emanondev.itemedit.storage.yaml;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class YmlServerStorage implements ServerStorage {

    private final YMLConfig database = ItemEdit.get()
            .getConfig("database" + File.separatorChar + "server-database.yml");
    private final Map<ItemStack, String> reversedMap =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();

    public YmlServerStorage() {
        reload();
    }

    @Override
    public ItemStack getItem(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        ItemStack item = database.getItemStack(id + ".item", null);
        return item == null ? null : item.clone();
    }

    @Override
    public String getNick(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        String nick = database.getMessage(id + ".nick", null, true);
        if (nick != null)
            return nick;
        if (!database.contains(id))
            return null;
        ItemStack item = getItem(id);
        if (!item.hasItemMeta())
            return item.getType().name().toLowerCase(Locale.ENGLISH);
        ItemMeta meta = ItemUtils.getMeta(item);
        if (!meta.hasDisplayName())
            return item.getType().name().toLowerCase(Locale.ENGLISH);
        return meta.getDisplayName();
    }

    @Override
    public void remove(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        reversedMap.remove(getItem(id));
        database.set(id, null);
        database.save();
    }

    @Override
    public void clear() {
        for (String key : database.getKeys(false))
            database.set(key, null);
        reversedMap.clear();
        database.save();
    }

    @Override
    public @NotNull Set<String> getIds() {
        return database.getKeys(false);
    }

    @Override
    public void setItem(@NotNull String id, @NotNull ItemStack item) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        if (item.getType() == Material.AIR)
            throw new IllegalArgumentException();
        item.setAmount(1);
        database.set(id + ".item", item);
        reversedMap.put(item, id);
        database.save();
    }

    @Override
    public void setNick(@NotNull String id, String nick) {
        validateID(id);
        if (!database.contains(id))
            return;
        id = id.toLowerCase(Locale.ENGLISH);
        database.set(id + ".nick", nick);
        database.save();
    }

    @Override
    @Nullable
    public String getId(ItemStack item) {
        if (item == null)
            return null;
        int amount = item.getAmount();
        if (item.getAmount() != 1)
            item.setAmount(1);
        String id = reversedMap.get(item);
        if (amount != 1)
            item.setAmount(amount);
        return id;
    }

    @Override
    public void reload() {
        reversedMap.clear();
        for (String id : database.getKeys(false)) {
            try {
                validateID(id);
            } catch (Exception e) {
                continue;
            }
            reversedMap.put(database.getItemStack(id + ".item", null), id);
        }
    }
}
