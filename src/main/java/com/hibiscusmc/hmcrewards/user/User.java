package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface User {
    @NotNull UUID uuid();

    @NotNull String name();

    @NotNull List<Reward> rewards();

    static @NotNull User user(final @NotNull UUID uuid, final @NotNull String name, final @NotNull List<Reward> rewards) {
        return new UserImpl(uuid, name, rewards);
    }

    static @NotNull User user(final @NotNull UUID uuid, final @NotNull String name) {
        return user(uuid, name, new ArrayList<>());
    }
}
