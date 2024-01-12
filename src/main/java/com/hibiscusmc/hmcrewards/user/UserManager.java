package com.hibiscusmc.hmcrewards.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {
    @Nullable User getCached(final @NotNull UUID id);

    default @Nullable User getCached(final @NotNull Player player) {
        return getCached(player.getUniqueId());
    }

    void removeCached(final @NotNull User user);

    void update(final @NotNull User user);

    @NotNull Collection<User> cached();

    void clearCache();

    @NotNull CompletableFuture<Void> saveAsync(final @NotNull User user);
}
