package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

final class UserImpl implements User {
    private final UUID uuid;
    private final String name;
    private final List<Reward> rewards;

    public UserImpl(final @NotNull UUID uuid, final @NotNull String name, final @NotNull List<Reward> rewards) {
        this.uuid = requireNonNull(uuid, "uuid");
        this.name = requireNonNull(name, "name");
        this.rewards = requireNonNull(rewards, "rewards");
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull List<Reward> rewards() {
        return rewards;
    }
}
