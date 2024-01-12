package com.hibiscusmc.hmcrewards.user.data.mongo;

import com.hibiscusmc.hmcrewards.data.serialize.bson.BsonCodecAdapter;
import com.hibiscusmc.hmcrewards.reward.RewardProviderRegistry;
import com.hibiscusmc.hmcrewards.user.User;
import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import com.hibiscusmc.hmcrewards.user.data.mongo.serialize.UserCodec;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class MongoUserDatastore implements UserDatastore {
    private final MongoCollection<User> collection;

    public MongoUserDatastore(final @NotNull MongoDatabase database, final @NotNull RewardProviderRegistry rewardProviderRegistry) {
        this.collection = database
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        database.getCodecRegistry(),
                        CodecRegistries.fromProviders(new CodecProvider() {
                            private final Codec<User> USER_CODEC = new BsonCodecAdapter<>(new UserCodec());
                            @Override
                            public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
                                // if class is User or subclass of User, return the UserCodec
                                if (User.class.isAssignableFrom(clazz)) {
                                    //noinspection unchecked
                                    return (Codec<T>) USER_CODEC;
                                }
                                return null;
                            }
                        })
                )).getCollection("users", User.class);
    }

    @Override
    public @Nullable User findByUuid(final @NotNull UUID uuid) {
        return collection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    @Override
    public @Nullable User findByUsername(final @NotNull String username) {
        return collection.find(Filters.eq("name", username)).first();
    }

    @Override
    public void save(final @NotNull User user) {
        collection.replaceOne(Filters.eq("uuid", user.uuid().toString()), user, new ReplaceOptions().upsert(true));
    }
}
