package com.hibiscusmc.hmcrewards.user.data.mongo;

import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.user.serialize.UserBsonCodec;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MongoUserDatastore implements UserDatastore {
    private final MongoDatabase database;
    private final MongoCollection<User> collection;

    public MongoUserDatastore(final @NotNull MongoDatabase database) {
        this.database = database.withCodecRegistry(CodecRegistries.fromCodecs(new UserBsonCodec()));
        this.collection = database.getCollection("users", User.class);
    }

    @Override
    public @Nullable User findByUuid(final @NotNull UUID uuid) {
        return collection.find(new BsonDocument().append("uuid", new BsonString(uuid.toString()))).first();
    }
}
