package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.user.data.UserDatastore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class UserManagerImpl implements UserManager {
    private final Map<UUID, User> cache = new HashMap<>();
    @Inject private Plugin plugin;
    @Inject private UserDatastore datastore;

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

    @Override
    public @NotNull CompletableFuture<Void> saveAsync(final @NotNull User user) {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                datastore.save(user);
                future.complete(null);
            } catch (final Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        return future;
    }
}
