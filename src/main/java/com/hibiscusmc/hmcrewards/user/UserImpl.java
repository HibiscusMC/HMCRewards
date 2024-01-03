package com.hibiscusmc.hmcrewards.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

final class UserImpl implements User {
    private final UUID uuid;
    private final String name;

    public UserImpl(final @NotNull UUID uuid, final @NotNull String name) {
        this.uuid = requireNonNull(uuid, "uuid");
        this.name = requireNonNull(name, "name");
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    @Override
    public @NotNull String name() {
        return name;
    }
}
