package com.hibiscusmc.hmcrewards.reward;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface RewardProviderRegistry {
    @Nullable Reward findByReference(final @NotNull String rewardRef);

    void register(final @NotNull RewardProvider<?> provider);

    @Nullable RewardProvider<?> provider(final @NotNull String id);

    @NotNull Collection<RewardProvider<?>> providers();
}
