package emanondev.itemedit.storage;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.jetbrains.annotations.NotNull;

public class MongoStorage {

    private final @NotNull MongoDatabase mongoDatabase;
    private final @NotNull MongoClient mongoClient;

    public MongoStorage(@NotNull String connectionString, @NotNull String database) {
        this.mongoClient = MongoClients.create(connectionString);
        this.mongoDatabase = this.mongoClient.getDatabase(database);
    }


}
