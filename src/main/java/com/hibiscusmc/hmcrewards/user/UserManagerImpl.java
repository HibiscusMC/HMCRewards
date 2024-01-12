package com.hibiscusmc.hmcrewards.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class UserManagerImpl implements UserManager {
    private final Map<UUID, User> cache = new HashMap<>();

    @Override
    public @Nullable User getCached(final @NotNull UUID id) {
        return cache.get(id);
    }

    @Override
    public void removeCached(final @NotNull User user) {
        cache.remove(user.uuid());
    }

    @Override
    public void update(final @NotNull User user) {
        cache.put(user.uuid(), user);
    }

    @Override
    public @NotNull Collection<User> cached() {
        return cache.values();
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
