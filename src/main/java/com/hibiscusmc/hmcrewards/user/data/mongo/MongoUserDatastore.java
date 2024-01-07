package com.hibiscusmc.hmcrewards.user.data.mongo;

import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.user.data.mongo.serialize.UserBsonCodec;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MongoUserDatastore implements UserDatastore {
    private final MongoCollection<User> collection;

    public MongoUserDatastore(final @NotNull MongoDatabase database) {
        this.collection = database.getCollection("users", User.class)
                .withCodecRegistry(CodecRegistries.fromCodecs(new UserBsonCodec()));
    }

    @Override
    public @Nullable User findByUuid(final @NotNull UUID uuid) {
        return collection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    @Override
    public void save(final @NotNull User user) {
        collection.replaceOne(Filters.eq("uuid", user.uuid().toString()),user, new ReplaceOptions().upsert(true));
    }
}
