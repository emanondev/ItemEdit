package emanondev.itemedit.storage.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

public class MongoStorage {

    private final @NotNull MongoDatabase mongoDatabase;
    private final @NotNull MongoClient mongoClient;

    private final @NotNull String collectionPrefix;

    public MongoStorage(@NotNull String connectionString, @NotNull String database, @NotNull String collectionPrefix) {
        this.mongoClient = MongoClients.create(connectionString);
        this.mongoDatabase = this.mongoClient.getDatabase(database);
        this.collectionPrefix = collectionPrefix;
    }

    public void close() {
        this.mongoClient.close();
    }

    public @NotNull MongoCollection<Document> getPlayerStorage() {
        return this.mongoDatabase.getCollection(this.collectionPrefix + "player-storage");
    }
}
