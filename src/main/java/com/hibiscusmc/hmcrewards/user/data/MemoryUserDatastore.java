package com.hibiscusmc.hmcrewards.user.data;

import com.hibiscusmc.hmcrewards.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class MemoryUserDatastore implements UserDatastore {
    private final Map<UUID, User> data = new ConcurrentHashMap<>();

    @Override
    public @Nullable User findByUuid(final @NotNull UUID uuid) {
        return data.get(uuid);
    }

    @Override
    public void save(final @NotNull User user) {
        data.put(user.uuid(), user);
    }
}
