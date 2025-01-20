package emanondev.itemedit.storage.mongo;

import com.mongodb.CursorType;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import emanondev.itemedit.storage.PlayerStorage;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoPlayerStorage implements PlayerStorage {

    private final @NotNull MongoStorage mongoStorage;
    private final @NotNull Logger logger;

    public MongoPlayerStorage(@NotNull MongoStorage mongoStorage, @NotNull Logger logger) {
        this.mongoStorage = mongoStorage;
        this.logger = logger;
    }

    private String getStore(OfflinePlayer player) {
        return storeByUUID() ? player.getUniqueId().toString() : player.getName();
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull OfflinePlayer player, @NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);

        Document document = this.mongoStorage.getPlayerStorage()
                .find(Filters.eq("store", this.getStore(player)))
                .cursorType(CursorType.NonTailable)
                .first();
        if (document == null) return null;

        Map<String, Object> serializedItem = document.get("items." + id, Document.class);
        if (serializedItem == null) return null;

        ItemStack item = ItemStack.deserialize(serializedItem);
        return item.clone();
    }

    @Override
    public void setItem(@NotNull OfflinePlayer player, @NotNull String id, @NotNull ItemStack item) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        if (item.getType() == Material.AIR)
            throw new IllegalArgumentException();
        item.setAmount(1);

        final Map<String, Object> serializedItem = item.serialize();
        final String store = this.getStore(player);
        this.mongoStorage.getPlayerStorage().updateOne(
                Filters.eq("store", store),
                new Document()
                        .append("$setOnInsert", new Document("store", store))
                        .append("$set", new Document("items." + id, serializedItem)),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void remove(@NotNull OfflinePlayer player, @NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        this.mongoStorage.getPlayerStorage().updateOne(
                Filters.eq("store", this.getStore(player)),
                new Document("$unset", new Document("items." + id, ""))
        );
    }

    @Override
    public void clear(@NotNull OfflinePlayer player) {
        String store = this.getStore(player);
        this.mongoStorage.getPlayerStorage().deleteOne(Filters.eq("store", store));
    }

    @Override
    public @NotNull Set<String> getIds(@NotNull OfflinePlayer player) {
        Document document = this.mongoStorage.getPlayerStorage()
                .find(Filters.eq("store", this.getStore(player)))
                .cursorType(CursorType.NonTailable)
                .first();
        if (document == null) return Collections.emptySet();

        Document itemsDocument = document.get("items", Document.class);
        return itemsDocument != null ? itemsDocument.keySet() : Collections.emptySet();
    }

    @Override
    public @NotNull Set<OfflinePlayer> getPlayers() {
        Set<String> playerData = this.mongoStorage.getPlayerStorage()
                .find()
                .cursorType(CursorType.NonTailable)
                .projection(Projections.include("store"))
                // TODO: Add limit?
                .map(document -> document.getString("store"))
                .into(new HashSet<>());
        Set<OfflinePlayer> players = new HashSet<>();
        boolean uuid = storeByUUID();
        for (String store : playerData) {
            UUID uniqueId;
            try {
                uniqueId = uuid ? UUID.fromString(store) : null;
            } catch (IllegalArgumentException e) {
                // Invalid uuid in config
                this.logger.log(Level.SEVERE, "Failed to validate uuid " + store + ", skipped player.", e);
                continue;
            }
            players.add(uuid ? Bukkit.getOfflinePlayer(uniqueId) : Bukkit.getOfflinePlayer(store));
        }
        return players;
    }
}
