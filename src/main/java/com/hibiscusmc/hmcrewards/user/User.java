package com.hibiscusmc.hmcrewards.user;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface User {
    @NotNull UUID uuid();

    @NotNull String name();

    @NotNull List<String> rewards();

    boolean hasReceivedRewardsBefore();

    void hasReceivedRewardsBefore(final boolean hasReceivedRewardsBefore);

    static @NotNull User user(final @NotNull UUID uuid, final @NotNull String name, final @NotNull List<String> rewards, final boolean hasReceivedRewardsBefore) {
        return new UserImpl(uuid, name, rewards, hasReceivedRewardsBefore);
    }

    static @NotNull User user(final @NotNull UUID uuid, final @NotNull String name) {
        return user(uuid, name, new ArrayList<>(), false);
    }
}
