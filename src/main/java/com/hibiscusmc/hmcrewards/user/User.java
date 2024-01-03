package com.hibiscusmc.hmcrewards.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface User {
    @NotNull UUID uuid();

    @NotNull String name();

    static @NotNull User user(final @NotNull UUID uuid, final @NotNull String name) {
        return new UserImpl(uuid, name);
    }
}
