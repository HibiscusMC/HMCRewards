package com.hibiscusmc.hmcrewards.user;

import com.hibiscusmc.hmcrewards.reward.Reward;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

final class UserImpl implements User {
    private final UUID uuid;
    private String name;
    private final List<Reward> rewards;
    private boolean hasReceivedRewardsBefore;

    public UserImpl(final @NotNull UUID uuid, final @NotNull String name, final @NotNull List<Reward> rewards, final boolean hasReceivedRewardsBefore) {
        this.uuid = requireNonNull(uuid, "uuid");
        this.name = requireNonNull(name, "name");
        this.rewards = requireNonNull(rewards, "rewards");
        this.hasReceivedRewardsBefore = hasReceivedRewardsBefore;
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
    public void name(final @NotNull String name) {
        this.name = name;
    }

    @Override
    public @NotNull List<Reward> rewards() {
        return rewards;
    }

    @Override
    public boolean hasReceivedRewardsBefore() {
        return hasReceivedRewardsBefore;
    }

    @Override
    public void hasReceivedRewardsBefore(final boolean hasReceivedRewardsBefore) {
        this.hasReceivedRewardsBefore = hasReceivedRewardsBefore;
    }
}
