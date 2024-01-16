package com.hibiscusmc.hmcrewards.command.arg;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

// just a helper object containing a string, to avoid a lot of stuff with command-flow
public final class RewardId {
    private final String id;

    public RewardId(final @NotNull String id) {
        this.id = requireNonNull(id, "id");
    }

    public @NotNull String id() {
        return id;
    }
}
