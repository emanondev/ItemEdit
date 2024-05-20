package emanondev.itemedit.storage.mongo;

import com.mongodb.CursorType;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import emanondev.itemedit.storage.ServerStorage;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MongoServerStorage implements ServerStorage {

    private final @NotNull MongoStorage mongoStorage;

    public MongoServerStorage(@NotNull MongoStorage mongoStorage) {
        this.mongoStorage = mongoStorage;
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        Document document = this.mongoStorage.getServerStorage()
                .find(Filters.eq("item_id", id))
                .projection(Projections.include("item"))
                .cursorType(CursorType.NonTailable)
                .first();
        if (document == null) return null;

        Map<String, Object> serializedItem = document.get("item", Document.class);
        return this.mapToItem(serializedItem);
    }

    private @Nullable ItemStack mapToItem(final @Nullable Map<String, Object> serializedItem) {
        if (serializedItem == null) return null;

        ItemStack item = ItemStack.deserialize(serializedItem);
        return item.clone();
    }

    @Override
    public @Nullable String getNick(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        Document document = this.mongoStorage.getServerStorage()
                .find(Filters.eq("item_id", id))
                .projection(Projections.include("nick", "item"))
                .cursorType(CursorType.NonTailable)
                .first();
        if (document == null) return null;

        String nick = document.getString("nick");
        if (nick != null) return nick;

        Map<String, Object> serializedItem = document.get("item", Document.class);
        ItemStack item = this.mapToItem(serializedItem);
        if (item == null) return null;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName()) return item.getType().name().toLowerCase(Locale.ENGLISH);

        return itemMeta.getDisplayName();
    }

    @Override
    public void setItem(@NotNull String id, @NotNull ItemStack item) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        if (item.getType() == Material.AIR)
            throw new IllegalArgumentException();
        item.setAmount(1);
        this.mongoStorage.getServerStorage().insertOne(new Document()
                .append("item_id", id)
                .append("item", item.serialize())
        );
    }

    @Override
    public void setNick(@NotNull String id, @Nullable String nick) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        this.mongoStorage.getServerStorage().updateOne(
                Filters.eq("item_id", id),
                nick != null ?
                        new Document("$set", new Document("nick", nick)) :
                        new Document("$unset", new Document("nick", ""))
        );
    }

    @Override
    public void remove(@NotNull String id) {
        validateID(id);
        id = id.toLowerCase(Locale.ENGLISH);
        this.mongoStorage.getServerStorage().deleteOne(Filters.eq("item_id", id));
    }

    @Override
    public void clear() {
        this.mongoStorage.getServerStorage().drop();
    }

    @Override
    public @NotNull Set<String> getIds() {
        return this.mongoStorage.getServerStorage()
                .find()
                .cursorType(CursorType.NonTailable)
                .projection(Projections.include("item_id"))
                .map(document -> document.getString("item_id"))
                .into(new HashSet<>());
    }

    @Override
    public String getId(ItemStack item) {
        item = item.clone();
        if (item.getAmount() != 1) item.setAmount(1);

        Map<String, Object> serializedItem = item.serialize();
        Document document = this.mongoStorage.getServerStorage()
                .find(Filters.eq("item", serializedItem))
                .cursorType(CursorType.NonTailable)
                .projection(Projections.include("item_id"))
                .first();
        return document != null ? document.getString("item_id") : null;
    }

    @Override
    public void reload() {
        // In MongoDB we don't cache the items so that there are no problems when using the same database on multiple servers.
        // In the future it is recommended to support message brokers like RabbitMQ, if something like that is supported, then you can also start caching the items for remote databases.
    }
}
